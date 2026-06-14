package cs.youtrade.autotrade.client.util.autotrade.endpoint.parent;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;

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
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/save"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<Boolean>>() {
                }.getType()).build().fetch();
    }
}
