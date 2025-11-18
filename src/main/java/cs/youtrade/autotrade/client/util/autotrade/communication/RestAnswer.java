package cs.youtrade.autotrade.client.util.autotrade.communication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestAnswer<T> {
    private final int status;
    private final T response;

    public RestAnswer(int status) {
        this(status, null);
    }
}
