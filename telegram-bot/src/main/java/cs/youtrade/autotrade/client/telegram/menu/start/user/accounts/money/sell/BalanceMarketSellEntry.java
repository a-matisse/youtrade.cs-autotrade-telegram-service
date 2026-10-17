package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.BalanceMarketEndpoint;
import org.springframework.stereotype.Service;

@Service
public class BalanceMarketSellEntry {
    private final BalanceMarketEndpoint endpoint;
    private final BalanceMarketSellRegistry registry;

    public BalanceMarketSellEntry(BalanceMarketEndpoint endpoint, BalanceMarketSellRegistry registry) {
        this.endpoint = endpoint;
        this.registry = registry;
    }

    public UserMenu start(UserData user) {
        var previous = registry.get(user);
        if (previous != null) previous.eraseSecrets();
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        load(user, data);
        if (data.getResult() == BalanceMarketSellData.Result.ACTIVE_LIST && data.getActive().isEmpty()) {
            data.setResult(null);
            return UserMenu.ACCOUNTS_SELL;
        }
        return UserMenu.ACCOUNTS_SELL_RESULT;
    }

    public void load(UserData user, BalanceMarketSellData data) {
        try {
            var answer = endpoint.active(user.getChatId());
            if (answer.getStatus() < 300 && answer.getResponse() != null
                    && answer.getResponse().isResult() && answer.getResponse().getData() != null) {
                data.setActive(answer.getResponse().getData());
                data.setSession(data.getActive().size() == 1 ? data.getActive().getFirst() : null);
                data.setResult(data.getSession() == null
                        ? BalanceMarketSellData.Result.ACTIVE_LIST
                        : BalanceMarketSellData.Result.SESSION);
                return;
            }
        } catch (RuntimeException ignored) { }
        data.setResult(BalanceMarketSellData.Result.UNAVAILABLE);
    }
}
