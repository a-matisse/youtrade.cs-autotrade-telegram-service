package cs.youtrade.autotrade.client.util.autotrade.endpoint.parent;

import cs.youtrade.autotrade.client.keygen.service.InnerKeyManagerService;
import cs.youtrade.autotrade.client.util.autotrade.communication.YtSyncRestClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public abstract class AbstractAtEndpoint implements AtCommunicationInt {
    public static final String YOUTRADE_KEY = "youtradeKey";
    private static final String API_KEY_HEADER = "X-Service-Key";

    @Autowired
    private InnerKeyManagerService keyManager;

    @Value("${yt.message.endpoint}")
    protected String atLink;
    protected YtSyncRestClient client;

    @PostConstruct
    public void init() {
        this.client = new YtSyncRestClient(atLink);
    }

    public Map<String, String> getHeaders() {
        var keyOpt = keyManager.findByKeyName(YOUTRADE_KEY);
        return keyOpt
                .map(innerKeyEntity ->
                        Map.of(API_KEY_HEADER, innerKeyEntity.getKeyValue()))
                .orElseGet(
                        Map::of);
    }

    public String createEndpoint(String sideEndpoint) {
        return getMainEndpoint() + sideEndpoint;
    }

    public String createEndpoint() {
        return getMainEndpoint();
    }
}
