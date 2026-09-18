package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.ref;

import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.ref.FcdRefDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.ref.FcdReferralBalanceDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.ref.FcdReferralBalanceOperationDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class RefEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdDefaultDto<FcdRefDto>> refGet(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.GET, createEndpoint())
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<FcdRefDto>>() {
                }.getType())
                .build()
                .fetch();
    }

    public RestAnswer<FcdDefaultDto<FcdRefDto>> refCreate(
            Long chatId
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/create"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<FcdRefDto>>() {
                }.getType())
                .build()
                .fetch();
    }

    public RestAnswer<FcdDefaultDto<FcdRefDto>> refConnect(
            Long chatId,
            String ref
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString(),
                "ref", ref
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/connect"))
                .headers(getHeaders())
                .params(params)
                .type(new TypeToken<FcdDefaultDto<FcdRefDto>>() {
                }.getType())
                .build()
                .fetch();
    }

    public RestAnswer<FcdReferralBalanceDto> getBalance(Long chatId) {
        return client.fetchFromApi(HttpMethod.GET, createEndpoint("/balance"))
                .headers(getHeaders())
                .params(Map.of("chatId", chatId.toString()))
                .type(FcdReferralBalanceDto.class)
                .build()
                .fetch();
    }

    public RestAnswer<FcdReferralBalanceOperationDto> transferBalance(
            Long chatId,
            BigDecimal amount,
            UUID requestId
    ) {
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/balance/transfer"))
                .headers(getHeaders())
                .params(Map.of(
                        "chatId", chatId.toString(),
                        "amount", amount.toPlainString(),
                        "requestId", requestId.toString()
                ))
                .type(FcdReferralBalanceOperationDto.class)
                .build()
                .fetch();
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/ref";
    }
}
