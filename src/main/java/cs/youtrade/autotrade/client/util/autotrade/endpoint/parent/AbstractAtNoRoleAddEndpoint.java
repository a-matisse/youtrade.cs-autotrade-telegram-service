package cs.youtrade.autotrade.client.util.autotrade.endpoint.parent;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.communication.HttpMethod;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;

import java.util.Map;

public abstract class AbstractAtNoRoleAddEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdDefaultDto<Boolean>> save(
            Long chatId,
            String token
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "token", token
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/save"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }
}
