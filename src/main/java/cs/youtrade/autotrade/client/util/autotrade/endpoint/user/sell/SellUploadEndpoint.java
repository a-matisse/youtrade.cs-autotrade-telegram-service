package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.communication.HttpMethod;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadGroupDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadInfoDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SellUploadEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdDefaultDto<List<FcdSellUploadGetDto>>> getUploadedItems(
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
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdDefaultDto<List<FcdSellUploadInfoDto>>> postUploadedItems(
            Long chatId,
            List<FcdSellUploadGroupDto> items
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
                new TypeToken<>() {
                }
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/sell/upload";
    }
}
