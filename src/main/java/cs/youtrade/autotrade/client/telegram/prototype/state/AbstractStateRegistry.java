package cs.youtrade.autotrade.client.telegram.prototype.state;

import cs.youtrade.autotrade.client.telegram.prototype.data.AbstractUserData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class AbstractStateRegistry<U extends AbstractUserData, B> {
    private final Map<U, B> builders = new ConcurrentHashMap<>();

    public B getOrCreateBuilder(U userData, Supplier<B> builderSupplier) {
        return builders.computeIfAbsent(userData, k -> builderSupplier.get());
    }

    public B completeAndRemove(U userData) {
        return builders.remove(userData);
    }

    public B getBuilder(U userData) {
        return builders.get(userData);
    }

    public void removeBuilder(U userData) {
        builders.remove(userData);
    }

    public boolean hasBuilder(U userData) {
        return builders.containsKey(userData);
    }
}
