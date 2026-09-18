package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.properties;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PropertiesEndpoint extends AbstractAtEndpoint {
    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/properties";
    }

    public RestAnswer<FcdDefaultDto<Boolean>> toggleBargainNotifications(Long chatId) {
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/toggle/bargain-notifications"))
                .headers(getHeaders())
                .params(Map.of("chatId", chatId.toString()))
                .type(new TypeToken<FcdDefaultDto<Boolean>>() {}.getType())
                .build().fetch();
    }
}
