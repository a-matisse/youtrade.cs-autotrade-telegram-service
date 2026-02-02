package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change.FcdSellChangeGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change.FcdSellChangePostDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SellChangeEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdDefaultDto<List<FcdSellChangeGetDto>>> getChanges(
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
                new TypeToken<FcdDefaultDto<List<FcdSellChangeGetDto>>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<FcdSellChangeGetDto>> getChangesGrouped(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.GET,
                createEndpoint("/groups"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<FcdSellChangeGetDto>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<Boolean>> postChanges(
            Long chatId,
            List<FcdSellChangePostDto> changes
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint(),
                getHeaders(),
                params,
                changes,
                new TypeToken<FcdDefaultDto<Boolean>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<Boolean>> postChangesGroups(
            Long chatId,
            List<FcdSellChangePostDto> changes
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/groups"),
                getHeaders(),
                params,
                changes,
                new TypeToken<FcdDefaultDto<Boolean>>() {
                }.getType()
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/sell/change";
    }
}
