package cs.youtrade.autotrade.client.util.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.gson.GsonConfig;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

@Log4j2
public abstract class AbstractRedisConsumerService<D> implements InitializingBean, DisposableBean {
    @Getter
    private static final Gson GSON = GsonConfig.createGson();
    private static final long MIN_IDLE_MILLIS = 60_000L;
    private static final long EMPTY_PARK_NANOS = TimeUnit.MILLISECONDS.toNanos(5);

    private final RedisTemplate<String, String> redisTemplate;
    private final StreamOperations<String, String, String> streamOps;

    private final String streamKey;
    private final String groupName;
    private final String consumerPrefix;
    private final int workerCount;
    private final int batchSize;
    private final long blockMillis;
    private final long pendingCheckIntervalMillis;

    private final IRedisConsumer<D> consumer;

    private final ExecutorService workers;
    private volatile boolean running = true;
    private final AtomicLong lastPendingCheck = new AtomicLong(0);

    public AbstractRedisConsumerService(
            IRedisConsumer<D> consumer,
            RedisTemplate<String, String> redisTemplate,
            String streamKey,
            String groupName,
            String consumerPrefix,
            int workerCount,
            int batchSize,
            long blockMillis,
            long pendingCheckIntervalMillis
    ) {
        this.consumer = consumer;
        this.redisTemplate = redisTemplate;
        this.streamOps = redisTemplate.opsForStream();
        this.streamKey = streamKey;
        this.groupName = groupName;
        this.consumerPrefix = consumerPrefix;
        this.workerCount = workerCount;
        this.batchSize = batchSize;
        this.blockMillis = blockMillis;
        this.pendingCheckIntervalMillis = pendingCheckIntervalMillis;
        this.workers = createWorkers(workerCount);
    }

    public AbstractRedisConsumerService(
            IRedisConsumer<D> consumer,
            RedisTemplate<String, String> redisTemplate,
            String streamKey,
            String groupName,
            String consumerPrefix
    ) {
        this(
                consumer,
                redisTemplate,
                streamKey,
                groupName,
                consumerPrefix,
                4,
                25,
                500,
                TimeUnit.SECONDS.toMillis(60)
        );
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
//          Обычно ошибка при уже существующей группе — игнорируем (без лишнего лога)
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

//      Небольшой локальный backoff при пустых ответах (в наносекундах)
        while (running) {
            try {
                List<MapRecord<String, String, String>> messages = streamOps.read(consumer, readOptions, readOffset);
                if (messages == null || messages.isEmpty()) {
                    long now = System.currentTimeMillis();
                    long prev = lastPendingCheck.get();
                    if (now - prev >= pendingCheckIntervalMillis && lastPendingCheck.compareAndSet(prev, now))
                        checkAndClaimPending(consumerName);

                    LockSupport.parkNanos(EMPTY_PARK_NANOS);
                    continue;
                }

                for (MapRecord<String, String, String> rec : messages) {
                    try {
                        payloadProcessProcedure(rec);
                    } catch (Exception e) {
//                      Критичная ошибка при обработке конкретной записи — логируем
//                      Не ACK'аем — останется в PEL и будет обработано позже
                        log.error("Processing failure for record id={} : {}", rec.getId(), e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                // серьёзная ошибка чтения из Redis — логируем и даём небольшой бэкофф
                log.error("Redis read error (consumer {}): {}", consumerName, e.getMessage(), e);
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
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
            long totalPending = summary == null
                    ? 0L
                    : summary.getTotalPendingMessages();
            if (totalPending == 0L)
                return;

            // если накопилось слишком много pending — логируем предупреждение (однократно пока ситуация не изменится)
            if (totalPending > 1000)
                log.warn("High pending count for group='{}' : {}", groupName, totalPending);

            try {
                List<RecordId> idsToClaim = streamOps
                        .pending(streamKey, groupName, Range.unbounded(), batchSize)
                        .stream()
                        .map(pm ->
                                RecordId.of(pm.getId().getValue())).toList();
                if (idsToClaim.isEmpty())
                    return;

                processIdsToClaim(consumerName, idsToClaim);
            } catch (Exception e) {
                // не хотим частые логи — только критично
                log.debug("Pending handling minor failure: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("Failed to inspect pending for stream='{}' group='{}' : {}", streamKey, groupName, e.getMessage(), e);
        }
    }

    private void processIdsToClaim(String consumerName, List<RecordId> idsToClaim) {
        try {
            RedisStreamCommands.XClaimOptions options = RedisStreamCommands
                    .XClaimOptions
                    .minIdle(Duration.ofMillis(MIN_IDLE_MILLIS))
                    .ids(idsToClaim.toArray(new RecordId[0]));
            List<MapRecord<String, String, String>> claimed =
                    streamOps.claim(streamKey, groupName, consumerName, options);

            if (claimed.isEmpty())
                return;

            claimed.forEach(this::payloadProcessProcedure);
        } catch (UnsupportedOperationException uoe) {
            // claim может быть не поддержан в конкретной версии API, игнорируем тихо
        }
    }

    private void payloadProcessProcedure(MapRecord<String, String, String> rec) {
        try {
//          СНАЧАЛА ACK - помечаем прочитанным
            streamOps.acknowledge(streamKey, groupName, rec.getId());
            processPayload(rec);

//          Если захочу сохранять - убрать delete
            redisTemplate.opsForStream().delete(streamKey, rec.getId());
        } catch (Exception e) {
            log.error("Failed to process message {}: {}", rec.getId(), e.getMessage(), e);
        }
    }

    private void processPayload(MapRecord<String, String, String> rec) {
        String payload = rec.getValue().get("payload");
        D data = getGSON().fromJson(payload, getType());
        consumer.consume(data);
    }

    private ExecutorService createWorkers(int workerCount) {
        return Executors.newFixedThreadPool(workerCount, r -> {
            Thread t = new Thread(r, "redis-stream-consumer");
            t.setDaemon(true);
            return t;
        });
    }

    public abstract TypeToken<D> getType();

    @Override
    public void destroy() {
        running = false;
        workers.shutdownNow();
        log.info("Redis stream consumer stopping: stream='{}', group='{}'", streamKey, groupName);
    }
}
