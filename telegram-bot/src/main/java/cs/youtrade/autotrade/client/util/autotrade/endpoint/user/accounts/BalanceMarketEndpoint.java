package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.BalanceMarketDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BalanceMarketEndpoint extends AbstractAtEndpoint {
    public RestAnswer<BalanceMarketDto.QuoteAnswer> quote(long chatId, BalanceMarketDto.QuoteInput input) {
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/quote"))
                .headers(getHeaders())
                .params(Map.of("chatId", Long.toString(chatId)))
                .body(input)
                .type(BalanceMarketDto.QuoteAnswer.class)
                .build()
                .fetch();
    }

    public RestAnswer<BalanceMarketDto.SessionAnswer> create(
            long chatId, String idempotencyKey, BalanceMarketDto.CreateInput input) {
        Map<String, String> headers = new HashMap<>(getHeaders());
        headers.put("Idempotency-Key", idempotencyKey);
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/sessions"))
                .headers(headers)
                .params(Map.of("chatId", Long.toString(chatId)))
                .body(input)
                .type(BalanceMarketDto.SessionAnswer.class)
                .build()
                .fetch();
    }

    public RestAnswer<BalanceMarketDto.ActiveAnswer> active(long chatId) {
        return client.fetchFromApi(HttpMethod.GET, createEndpoint("/sessions/active"))
                .headers(getHeaders())
                .params(Map.of("chatId", Long.toString(chatId)))
                .type(BalanceMarketDto.ActiveAnswer.class)
                .build()
                .fetch();
    }

    public RestAnswer<BalanceMarketDto.SessionAnswer> get(long chatId, long sessionId) {
        return client.fetchFromApi(HttpMethod.GET, createEndpoint("/sessions/" + sessionId))
                .headers(getHeaders())
                .params(Map.of("chatId", Long.toString(chatId)))
                .type(BalanceMarketDto.SessionAnswer.class)
                .build()
                .fetch();
    }

    public RestAnswer<BalanceMarketDto.SessionAnswer> cancel(long chatId, long sessionId) {
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/sessions/" + sessionId + "/cancel"))
                .headers(getHeaders())
                .params(Map.of("chatId", Long.toString(chatId)))
                .type(BalanceMarketDto.SessionAnswer.class)
                .build()
                .fetch();
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/balance-market";
    }
}
