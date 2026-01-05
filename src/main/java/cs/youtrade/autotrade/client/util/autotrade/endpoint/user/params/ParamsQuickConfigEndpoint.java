package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.communication.HttpMethod;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsQuickConfigInitDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ParamsQuickConfigEndpoint extends AbstractAtEndpoint {
    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/params/quick-config";
    }

    public RestAnswer<FcdDefaultDto<Long>> init(
            Long chatId,
            FcdParamsQuickConfigInitDto qcInit
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint(),
                getHeaders(),
                params,
                qcInit,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdDefaultDto<Long>> disable(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.DELETE,
                createEndpoint(),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }
}
