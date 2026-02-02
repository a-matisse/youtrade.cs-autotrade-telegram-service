package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.SellPriceEvalMode;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.FcdSellHistoryFullDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait.FcdSellWaitFullDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SellDefaultEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdDefaultDto<Boolean>> toggle(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint("/toggle"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<Boolean>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<SellPriceEvalMode>> switchEvalMode(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.PUT,
                createEndpoint("/eval-mode"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<SellPriceEvalMode>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdDefaultDto<Boolean>> switchEvalModeS1(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.PUT,
                createEndpoint("/eval-mode/s1"),
                getHeaders(),
                params,
                new TypeToken<FcdDefaultDto<Boolean>>() {
                }.getType()
        );
    }

    public RestAnswer<FcdSellHistoryFullDto> getSellHistory(
            Long chatId,
            Integer days
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "days", days.toString()
        );
        return client.fetchFromApi(
                HttpMethod.GET,
                createEndpoint("/history"),
                getHeaders(),
                params,
                new TypeToken<FcdSellHistoryFullDto>() {
                }.getType()
        );
    }

    public RestAnswer<FcdSellWaitFullDto> getSellWaiting(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.GET,
                createEndpoint("/waiting"),
                getHeaders(),
                params,
                new TypeToken<FcdSellWaitFullDto>() {
                }.getType()
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/sell";
    }
}
