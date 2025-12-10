package cs.youtrade.autotrade.client.util.redis;

public interface IRedisConsumer<D> {
    void consume(D data);
}
