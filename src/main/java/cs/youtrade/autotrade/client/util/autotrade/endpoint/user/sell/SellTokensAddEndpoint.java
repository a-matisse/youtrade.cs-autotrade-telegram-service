package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.communication.HttpMethod;
import cs.youtrade.autotrade.client.util.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.SellTokenAddDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.FcdSellTokensAddDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import org.springframework.stereotype.Component;

@Component
public class SellTokensAddEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdSellTokensAddDto> addToken(
            Long chatId,
            String token
    ) {
        SellTokenAddDto dto = new SellTokenAddDto(chatId, token);
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint(),
                getHeaders(),
                dto,
                new TypeToken<FcdSellTokensAddDto>() {
                }.getType()
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/sell/token";
    }
}
