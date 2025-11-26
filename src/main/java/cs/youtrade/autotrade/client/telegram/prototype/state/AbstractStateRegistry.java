package cs.youtrade.autotrade.client.telegram.prototype.state;

import cs.youtrade.autotrade.client.telegram.prototype.data.AbstractUserData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class AbstractStateRegistry<U extends AbstractUserData, B> implements IStateRegistry<U, B> {
    private final Map<U, B> builders = new ConcurrentHashMap<>();

    public B getOrCreate(U userData, Supplier<B> dataSupplier) {
        return builders.computeIfAbsent(userData, k -> dataSupplier.get());
    }

    public B get(U userData) {
        return builders.remove(userData);
    }

    public B remove(U userData) {
        return builders.get(userData);
    }

    public boolean exists(U userData) {
        return builders.containsKey(userData);
    }
}
