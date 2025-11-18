package cs.youtrade.autotrade.client.util.autotrade.communication.util;

public class YtRestClientException extends RuntimeException {
    public YtRestClientException(String message) {
        super(message);
    }

    public YtRestClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
