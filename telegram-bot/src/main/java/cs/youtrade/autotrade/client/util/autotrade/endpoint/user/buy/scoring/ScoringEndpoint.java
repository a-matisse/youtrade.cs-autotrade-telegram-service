package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.scoring;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.ItemScoringType;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.buy.FcdScoringUpdateDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScoringEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdDefaultDto<Long>> addScoring(
            Long chatId,
            Double minProfit,
            ItemScoringType type
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "minProfit", minProfit.toString(),
                "type", type.name()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint())
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<Long>>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdScoringUpdateDto> editScoring(
            Long chatId,
            Long scoringId,
            String field,
            String value
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "scoringId", scoringId.toString(),
                "field", field,
                "value", value
        );
        return client.fetchFromApi(HttpMethod.PUT, createEndpoint())
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdScoringUpdateDto>() {
                }.getType()).build().fetch();
    }

    public RestAnswer<FcdDefaultDto<Long>> deleteScoring(
            Long chatId,
            Long scoringId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "scoringId", scoringId.toString()
        );
        return client.fetchFromApi(HttpMethod.DELETE, createEndpoint())
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<Long>>() {
                }.getType()).build().fetch();
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/buy/scoring";
    }
}
