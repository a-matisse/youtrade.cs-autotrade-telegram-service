package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.profit;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.ItemScoringType;
import cs.youtrade.autotrade.client.util.autotrade.communication.HttpMethod;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProfitEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdDefaultDto<Long>> addProfit(
            Long chatId,
            Double minProfit,
            ItemScoringType type
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "minProfit", minProfit.toString(),
                "type", type.name()
        );
        return client.fetchFromApi(
                HttpMethod.POST,
                createEndpoint(),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdDefaultDto<Long>> editProfit(
            Long chatId,
            Long profitId,
            String field,
            String value
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "profitId", profitId.toString(),
                "field", field,
                "value", value
        );
        return client.fetchFromApi(
                HttpMethod.PUT,
                createEndpoint(),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    public RestAnswer<FcdDefaultDto<Long>> deleteProfit(
            Long chatId,
            Long profitId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "profitId", profitId.toString()
        );
        return client.fetchFromApi(
                HttpMethod.DELETE,
                createEndpoint(),
                getHeaders(),
                params,
                new TypeToken<>() {
                }
        );
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/buy/profit";
    }
}
