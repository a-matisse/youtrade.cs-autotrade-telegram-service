package cs.youtrade.autotrade.client.telegram.messaging.redis;

import com.google.gson.Gson;
import cs.youtrade.autotrade.client.telegram.messaging.TelegramUpdReceiverService;
import cs.youtrade.autotrade.client.util.gson.GsonConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

@Service
@Log4j2
public class TelegramRedisStreamConsumerService implements InitializingBean, DisposableBean {
    private static final Gson GSON = GsonConfig.createGson();

    private final RedisTemplate<String, String> redisTemplate;
    private final StreamOperations<String, String, String> streamOps;

    private final String streamKey = "telegram-updates";
    private final String groupName;
    private final String consumerPrefix;
    private final int workerCount;
    private final int batchSize;
    private final long blockMillis;
    private final long pendingCheckIntervalMillis;

    private final ExecutorService workers;
    private volatile boolean running = true;
    private final AtomicLong lastPendingCheck = new AtomicLong(0);

    private final TelegramUpdReceiverService receiver;

    @Autowired
    public TelegramRedisStreamConsumerService(
            RedisTemplate<String, String> redisTemplate,
            @Value("${telegram.redis.group}") String groupName,
            @Value("${telegram.redis.consumerPrefix}") String consumerPrefix,
            @Value("${telegram.redis.workerCount}") int workerCount,
            @Value("${telegram.redis.batchSize}") int batchSize,
            @Value("${telegram.redis.blockMillis}") long blockMillis,
            @Value("${telegram.redis.pendingCheckIntervalMillis}") long pendingCheckIntervalMillis,

            TelegramUpdReceiverService receiver
    ) {
        this.redisTemplate = redisTemplate;
        this.streamOps = redisTemplate.opsForStream();
        this.groupName = groupName;
        this.consumerPrefix = consumerPrefix;
        this.workerCount = Math.max(1, workerCount);
        this.batchSize = Math.max(1, batchSize);
        this.blockMillis = Math.max(0, blockMillis);
        this.pendingCheckIntervalMillis = Math.max(1_000, pendingCheckIntervalMillis);
        this.workers = Executors.newFixedThreadPool(this.workerCount, r -> {
            Thread t = new Thread(r, "redis-stream-consumer");
            t.setDaemon(true);
            return t;
        });

        this.receiver = receiver;
    }

    @Override
    public void afterPropertiesSet() {
        ensureGroupExistsOnce();
        log.info("Redis stream consumer starting: stream='{}', group='{}', workers={}, batchSize={}",
                streamKey, groupName, workerCount, batchSize);
        for (int i = 0; i < workerCount; i++) {
            final int idx = i;
            workers.submit(() -> runWorker(idx));
        }
    }

    private void ensureGroupExistsOnce() {
        try {
            streamOps.createGroup(streamKey, ReadOffset.from("0-0"), groupName);
            log.info("Created consumer group '{}' on stream '{}'", groupName, streamKey);
        } catch (Exception ex) {
            // обычно ошибка при уже существующей группе — игнорируем (без лишнего лога)
            // если другая ошибка — залогируем как ошибка
            if (!ex.getMessage().toLowerCase().contains("busygroup"))
                log.error("Failed to create consumer group: {}", ex.getMessage());
        }
    }

    private void runWorker(int idx) {
        final String consumerName = consumerPrefix + "-" + UUID.randomUUID().toString().substring(0, 8) + "-" + idx;
        final Consumer consumer = Consumer.from(groupName, consumerName);

        final StreamReadOptions readOptions = StreamReadOptions.empty()
                .count(batchSize)
                .block(Duration.ofMillis(blockMillis));

        final StreamOffset<String> readOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());

        // небольшой локальный backoff при пустых ответах (в наносекундах)
        final long parkNanosWhenEmpty = Math.max(1L, java.util.concurrent.TimeUnit.MILLISECONDS.toNanos(5));

        while (running) {
            try {
                List<MapRecord<String, String, String>> messages = streamOps.read(consumer, readOptions, readOffset);

                if (messages == null || messages.isEmpty()) {
                    long now = System.currentTimeMillis();
                    long prev = lastPendingCheck.get();
                    if (now - prev >= pendingCheckIntervalMillis &&
                            lastPendingCheck.compareAndSet(prev, now)) {
                        checkAndClaimPending(consumerName);
                    }
                    // лёгкая пауза (LockSupport)
                    LockSupport.parkNanos(parkNanosWhenEmpty);
                    continue;
                }

                for (MapRecord<String, String, String> rec : messages) {
                    try {
                        processPayload(rec);
                    } catch (Exception e) {
                        // критичная ошибка при обработке конкретной записи — логируем
                        log.error("Processing failure for record id={} : {}", rec.getId(), e.getMessage(), e);
                        // не ACK'аем — останется в PEL и будет обработано позже
                    }
                }
            } catch (Exception e) {
                // серьёзная ошибка чтения из Redis — логируем и даём небольшой бэкофф
                log.error("Redis read error (consumer {}): {}", consumerName, e.getMessage(), e);
                LockSupport.parkNanos(java.util.concurrent.TimeUnit.MILLISECONDS.toNanos(100));
            }
        }
    }

    /**
     * Простая, редкая проверка Pending + попытка claim старых записей.
     * Буферирует вызовы (через lastPendingCheck), чтобы не спамить Redis.
     */
    private void checkAndClaimPending(String consumerName) {
        try {
            PendingMessagesSummary summary = streamOps.pending(streamKey, groupName);
            long totalPending = summary == null ? 0L : summary.getTotalPendingMessages();
            if (totalPending == 0L) return;

            // если накопилось слишком много pending — логируем предупреждение (однократно пока ситуация не изменится)
            if (totalPending > 1000)
                log.warn("High pending count for group='{}' : {}", groupName, totalPending);

            // Попробуем забрать (claim) до batchSize старых сообщений idle >= 60s
            long minIdleMillis = 60_000L;
            try {
                // API claim через spring-data может иметь разные сигнатуры; пробуем high-level claim
                // xautoclaim удобнее, но не всегда доступен в версии spring-data — используем claim с 0 как пример
                List<RecordId> idsToClaim = streamOps.pending(streamKey, groupName, Range.unbounded(), batchSize)
                        .stream().map(pm -> RecordId.of(pm.getId().getValue())).toList();

                if (idsToClaim.isEmpty())
                    return;

                try {
                    RedisStreamCommands.XClaimOptions options = RedisStreamCommands
                            .XClaimOptions
                            .minIdle(Duration.ofMillis(minIdleMillis))
                            .ids(idsToClaim.toArray(new RecordId[0]));
                    List<MapRecord<String, String, String>> claimed =
                            streamOps.claim(streamKey, groupName, consumerName, options);

                    if (claimed.isEmpty())
                        return;

                    for (MapRecord<String, String, String> rec : claimed) {
                        try {
                            // СНАЧАЛА ACK - помечаем прочитанным
                            streamOps.acknowledge(streamKey, groupName, rec.getId());
                            // ПОТОМ обработка
                            processPayload(rec);
                        } catch (Exception inner) {
                            log.error("Error processing claimed record {} : {}", rec.getId(), inner.getMessage(), inner);
                        }
                    }
                } catch (UnsupportedOperationException uoe) {
                    // claim может быть не поддержан в конкретной версии API, игнорируем тихо
                }
            } catch (Exception e) {
                // не хотим частые логи — только критично
                log.debug("Pending handling minor failure: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("Failed to inspect pending for stream='{}' group='{}' : {}", streamKey, groupName, e.getMessage(), e);
        }
    }

    private void processPayload(
            MapRecord<String, String, String> rec
    ) {

        try {
            String payload = rec.getValue().get("payload");
            Update update = GSON.fromJson(payload, Update.class);
            receiver.consume(update);

//          Если захочу сохранять
//          streamOps.acknowledge(streamKey, groupName, rec.getId());
            redisTemplate.opsForStream().delete(streamKey, rec.getId());
        } catch (Exception e) {
            log.error("Failed to process message {}: {}", rec.getId(), e.getMessage(), e);
        }
    }

    @Override
    public void destroy() throws Exception {
        running = false;
        workers.shutdownNow();
        // одноразовый лог остановки
        log.info("Redis stream consumer stopping: stream='{}', group='{}'", streamKey, groupName);
    }
}
