package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdMarketCsgoBalanceTransferDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdMarketCsgoMoneySendDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto.FcdMarketCsgoBalanceTransferInput;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto.FcdMarketCsgoMoneySendInput;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MarketCsgoBalanceEndpoint extends AbstractAtEndpoint {
    public RestAnswer<FcdMarketCsgoBalanceTransferDto> balanceTransfer(
            Long chatId,
            FcdMarketCsgoBalanceTransferInput input
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/balance-transfer"))
                .headers(getHeaders())
                .params(params)
                .body(input)
                .type(FcdMarketCsgoBalanceTransferDto.class)
                .build()
                .fetch();
    }

    public RestAnswer<FcdMarketCsgoMoneySendDto> moneySend(
            Long chatId,
            FcdMarketCsgoMoneySendInput input
    ) {
        Map<String, String> params = Map.of(
                "chatId", chatId.toString()
        );
        return client.fetchFromApi(HttpMethod.POST, createEndpoint("/money-send"))
                .headers(getHeaders())
                .params(params)
                .body(input)
                .type(FcdMarketCsgoMoneySendDto.class)
                .build()
                .fetch();
    }

    @Override
    public String getMainEndpoint() {
        return "/api/telegram/user/accounts/v2/market-csgo";
    }
}
