package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.communication.HttpMethod;
import cs.youtrade.autotrade.client.util.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.DeleteAnsDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict.FcdSellRestrictGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict.FcdSellRestrictPostDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SellRestrictEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdDefaultDto<List<FcdSellRestrictGetDto>>> getRestrictions(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.GET,
                createEndpoint(),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<List<FcdSellRestrictGetDto>>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<List<DeleteAnsDto>>> postRestrictions(
            Long chatId,
            List<FcdSellRestrictPostDto> restrictions
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint(),
                getHeaders(),
                params,
                restrictions,
                new TypeToken<FcdDefaultDto<List<DeleteAnsDto>>>() {
                }.getType()
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/sell/restrictions";
    }
}
