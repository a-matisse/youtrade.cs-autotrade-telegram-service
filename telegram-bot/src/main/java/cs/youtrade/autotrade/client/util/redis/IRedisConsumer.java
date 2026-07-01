package cs.youtrade.autotrade.client.util.redis;

public interface IRedisConsumer<D> {
    boolean consume(String payload, D data);

    boolean shouldDeserialize();
}
