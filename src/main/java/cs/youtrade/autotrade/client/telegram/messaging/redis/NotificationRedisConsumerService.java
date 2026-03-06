package cs.youtrade.autotrade.client.telegram.messaging.redis;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.telegram.messaging.receiver.NotificationReceiverService;
import cs.youtrade.autotrade.client.util.notification.YouTradeNotification;
import cs.youtrade.autotrade.client.util.redis.AbstractRedisConsumerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationRedisConsumerService extends AbstractRedisConsumerService<YouTradeNotification> {
    public NotificationRedisConsumerService(
            NotificationReceiverService consumer,
            RedisTemplate<String, String> redisTemplate,
            @Value("${youtrade.redis.stream.notification.name}") String streamKey,
            @Value("${youtrade.redis.stream.notification.group}") String groupName,
            @Value("${telegram.redis.consumerPrefix}") String consumerPrefix
    ) {
        super(consumer, redisTemplate, streamKey, groupName, consumerPrefix);
    }

    @Override
    public TypeToken<YouTradeNotification> getType() {
        return TypeToken.get(YouTradeNotification.class);
    }
}
