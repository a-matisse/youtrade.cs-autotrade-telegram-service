package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsQuickConfigInitDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
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
        return client.fetchFromApi(HttpMethod.POST, createEndpoint())
                .headers(getHeaders())
                .params(params)
                .body(qcInit)
                .type(new TypeToken<FcdDefaultDto<Long>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<Long>> disable(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.DELETE, createEndpoint())
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<Long>>() {
                }.getType()).build().fetch();
    }
}
