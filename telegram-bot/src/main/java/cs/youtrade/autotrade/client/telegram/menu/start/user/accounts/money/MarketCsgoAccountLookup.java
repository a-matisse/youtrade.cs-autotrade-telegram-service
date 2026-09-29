package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.util.accounts.FcdAccountV2Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MarketCsgoAccountLookup {
    private final AccountsV2Endpoint accountsEndpoint;
    private final ParamsEndpoint paramsEndpoint;
    private final YTPPageProcessor pageProcessor;

    public String currentPageBalances(long chatId) {
        String list = pageProcessor.getPage(chatId).fcd().getAccounts().stream()
                .filter(account -> account.getSellBalance() != null)
                .sorted(Comparator.comparingLong(FcdAccountV2Dto::getId))
                .map(account -> {
                    Double available = account.getSellBalance().getAvailable();
                    String balance = available == null ? "—" : "$" + BigDecimal.valueOf(available)
                            .setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
                    return "<code>" + account.getId() + "</code> — <b>" + balance + "</b>";
                })
                .collect(Collectors.joining("\n"));
        return list.isBlank() ? "Нет аккаунтов продажи Market.CSGO" : list;
    }

    public boolean isMarketCsgo(long chatId) {
        var answer = paramsEndpoint.getCurrent(chatId);
        if (answer.getStatus() >= 300 || answer.getResponse() == null
                || !answer.getResponse().isResult() || answer.getResponse().getData() == null)
            throw new IllegalStateException("Не удалось проверить направление торговли.");
        return answer.getResponse().getData().getDestination() == MarketType.MARKET_CSGO;
    }

    public Account find(long chatId, long tokenId) {
        for (int page = 0; ; page++) {
            var answer = accountsEndpoint.getAccountsPage(chatId, page, 50);
            if (answer.getStatus() >= 300 || answer.getResponse() == null || !answer.getResponse().isResult())
                throw new IllegalStateException("Не удалось получить список аккаунтов.");
            var accounts = answer.getResponse().getAccounts();
            if (accounts == null) throw new IllegalStateException("Список аккаунтов недоступен.");
            for (FcdAccountV2Dto account : accounts) {
                if (account.getId() != null && account.getId() == tokenId) {
                    if (account.getSellBalance() == null)
                        return new Account(account.getId(), null);
                    Double available = account.getSellBalance().getAvailable();
                    return new Account(account.getId(), available == null ? null : BigDecimal.valueOf(available));
                }
            }
            if (!accounts.hasNext()) return null;
        }
    }

    public record Account(Long id, BigDecimal available) { }
}
