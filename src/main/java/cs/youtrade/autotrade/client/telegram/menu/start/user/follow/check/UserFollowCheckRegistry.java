package cs.youtrade.autotrade.client.telegram.menu.start.user.follow.check;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.state.IStateRegistry;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class UserFollowCheckRegistry implements IStateRegistry<UserData, UserFollowCheckData> {
    private final Map<UserData, Queue<UserFollowCheckData>> userDataQueueMap = new ConcurrentHashMap<>();

    @Override
    public UserFollowCheckData getOrCreate(UserData userData, Supplier<UserFollowCheckData> dataSupplier) {
        Queue<UserFollowCheckData> queue = userDataQueueMap.computeIfAbsent(userData, k -> new LinkedList<>());
        if (queue.isEmpty()) {
            UserFollowCheckData newData = dataSupplier.get();
            queue.offer(newData);
            return newData;
        }

        return queue.peek();
    }

    @Override
    public UserFollowCheckData get(UserData userData) {
        Queue<UserFollowCheckData> queue = userDataQueueMap.get(userData);
        return queue != null && !queue.isEmpty()
                ? queue.peek()
                : null;
    }

    @Override
    public UserFollowCheckData remove(UserData userData) {
        Queue<UserFollowCheckData> queue = userDataQueueMap.get(userData);
        if (queue != null && !queue.isEmpty()) {
            UserFollowCheckData removed = queue.poll();
            if (queue.isEmpty())
                userDataQueueMap.remove(userData);

            return removed;
        }
        return null;
    }

    @Override
    public boolean exists(UserData userData) {
        Queue<UserFollowCheckData> queue = userDataQueueMap.get(userData);
        return queue != null && !queue.isEmpty();
    }
}
