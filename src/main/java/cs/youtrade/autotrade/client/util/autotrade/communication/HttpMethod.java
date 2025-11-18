package cs.youtrade.autotrade.client.util.autotrade.communication;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum HttpMethod {
    GET,
    HEAD,
    POST(true),
    PUT(true),
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH(true);

    private final boolean allowBody;

    HttpMethod() {
        this.allowBody = false;
    }

    public boolean allowsBody() {
        return allowBody;
    }
}
