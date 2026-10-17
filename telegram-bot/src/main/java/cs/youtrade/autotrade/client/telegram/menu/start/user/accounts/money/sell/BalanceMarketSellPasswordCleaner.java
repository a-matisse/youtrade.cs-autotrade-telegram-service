package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BalanceMarketSellPasswordCleaner {
    private final Set<BalanceMarketSellData> forms = ConcurrentHashMap.newKeySet();

    public void track(BalanceMarketSellData data) {
        forms.add(data);
    }

    @Scheduled(fixedDelay = 10_000)
    public void clearExpiredPasswords() {
        for (var data : forms) {
            synchronized (data) {
                if (data.getPassword() == null) {
                    forms.remove(data);
                } else if (data.getQuote() == null || BalanceMarketSellData.expired(data.getQuote())) {
                    data.eraseSecrets();
                    data.setQuote(null);
                    forms.remove(data);
                }
            }
        }
    }
}
