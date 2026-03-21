package cs.youtrade.autotrade.client.telegram.prototype;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserRegistry {
    private final Map<Long, UserData> userMap = new ConcurrentHashMap<>();

    public UserData getUser(long chatId) {
        return userMap.computeIfAbsent(chatId, UserData::new);
    }
}
