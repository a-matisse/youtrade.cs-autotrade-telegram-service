package cs.youtrade.autotrade.client.util.redis;

public interface IRedisConsumer<D> {
    void consume(String payload, D data);

    boolean shouldDeserialize();
}
