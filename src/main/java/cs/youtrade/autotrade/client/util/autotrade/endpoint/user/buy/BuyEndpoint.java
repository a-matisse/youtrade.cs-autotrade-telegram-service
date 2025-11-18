package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.communication.HttpMethod;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.BuyTokenAddDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;

public class BuyEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdDefaultDto<Long>> buyTokensAdd(
            long chatId,
            String api,
            String partnerId,
            String steamToken
    ) {
        BuyTokenAddDto dto = new BuyTokenAddDto(chatId, api, partnerId, steamToken);
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/token"),
                getHeaders(),
                dto,
                new TypeToken<>() {
                }
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/buy";
    }
}
