package cs.youtrade.autotrade.client.telegram.prototype;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class UserRegistry {
    private final Map<Long, UserData> userMap = new ConcurrentHashMap<>();

    public UserData put(Long chatId, Function<Long, UserData> mapper) {
        var user = mapper.apply(chatId);
        userMap.put(chatId, user);
        return user;
    }

    public UserData getOrCreateUser(long chatId, Function<Long, UserData> mapper) {
        return userMap.computeIfAbsent(chatId, mapper);
    }
}
