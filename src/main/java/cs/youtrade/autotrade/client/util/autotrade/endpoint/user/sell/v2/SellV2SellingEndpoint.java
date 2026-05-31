package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.v2;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetFullDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.selling.FcdSellingV2PostGroupDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SellV2SellingEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdSellListGetFullDto> getSelling(
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
                new TypeToken<FcdSellListGetFullDto>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<Boolean>> postSelling(
            Long chatId,
            List<FcdSellingV2PostGroupDto> items
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint(),
                getHeaders(),
                params,
                items,
                new TypeToken<FcdDefaultDto<Boolean>>() {
                }.getType()
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/v2/selling";
    }
}
