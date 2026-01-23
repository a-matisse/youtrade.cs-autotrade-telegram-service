package cs.youtrade.autotrade.client.util.autotrade.endpoint.norole;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.communication.HttpMethod;
import cs.youtrade.autotrade.client.util.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.norole.FcdGetPricesDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.norole.FcdTopUpDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SubGetEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdGetPricesDto> getPrices(
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
                new TypeToken<FcdGetPricesDto>() {
                }.getType()
        );
    }

    public RestAnswer<FcdTopUpDto> topUp(
            Long chatId,
            Double amount
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "amount", String.valueOf(amount)
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint(),
                getHeaders(),
                params,
                new TypeToken<FcdTopUpDto>() {
                }.getType()
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/no-role/pay";
    }
}
