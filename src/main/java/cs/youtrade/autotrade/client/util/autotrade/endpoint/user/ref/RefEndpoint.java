package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.ref;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.communication.HttpMethod;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.ref.FcdRefDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RefEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdDefaultDto<FcdRefDto>> refCreate(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/create"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdDefaultDto<FcdRefDto>> refConnect(
            Long chatId,
            String ref
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "ref", ref
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/connect"),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/ref";
    }
}
