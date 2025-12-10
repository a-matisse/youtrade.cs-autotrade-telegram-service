package cs.youtrade.autotrade.client.telegram.messaging.redis;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.telegram.messaging.TelegramUpdReceiverService;
import cs.youtrade.autotrade.client.util.redis.AbstractRedisConsumerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Log4j2
public class TelegramRedisStreamConsumerService extends AbstractRedisConsumerService<Update> {
    @Autowired
    public TelegramRedisStreamConsumerService(
            TelegramUpdReceiverService consumer,
            RedisTemplate<String, String> redisTemplate,
            @Value("${youtrade.telegram.redis.stream.name}") String streamKey,
            @Value("${youtrade.telegram.redis.stream.group}") String groupName,
            @Value("${telegram.redis.consumerPrefix}") String consumerPrefix
    ) {
        super(
                consumer,
                redisTemplate,
                streamKey,
                groupName,
                consumerPrefix
        );
    }

    @Override
    public TypeToken<Update> getType() {
        return TypeToken.get(Update.class);
    }
}
