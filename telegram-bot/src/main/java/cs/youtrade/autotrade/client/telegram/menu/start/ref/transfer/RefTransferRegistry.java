package cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefTransferRegistry {
    private static final String KEY_PREFIX = "telegram:ref-transfer:";
    private static final Duration TTL = Duration.ofDays(7);

    private final RedisTemplate<String, String> redisTemplate;

    public RefTransferData create(UserData user, BigDecimal amount) {
        RefTransferData data = new RefTransferData(UUID.randomUUID(), amount);
        redisTemplate.opsForValue().set(key(user), serialize(data), TTL);
        return data;
    }

    public Optional<RefTransferData> get(UserData user) {
        String value = redisTemplate.opsForValue().get(key(user));
        if (value == null || value.isBlank())
            return Optional.empty();
        try {
            String[] parts = value.split("\\|", 2);
            return Optional.of(new RefTransferData(
                    UUID.fromString(parts[0]),
                    new BigDecimal(parts[1])
            ));
        } catch (RuntimeException ignored) {
            clear(user);
            return Optional.empty();
        }
    }

    public void clear(UserData user) {
        redisTemplate.delete(key(user));
    }

    private String key(UserData user) {
        return KEY_PREFIX + user.getChatId();
    }

    private String serialize(RefTransferData data) {
        return data.requestId() + "|" + data.amount().toPlainString();
    }
}
