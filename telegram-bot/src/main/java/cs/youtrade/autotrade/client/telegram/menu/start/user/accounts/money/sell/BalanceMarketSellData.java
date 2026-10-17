package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell;

import lombok.Getter;
import lombok.Setter;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.BalanceMarketDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Getter
@Setter
public class BalanceMarketSellData {
    public enum Result { SESSION, ACTIVE_LIST, UNAVAILABLE, UNKNOWN, REJECTED }

    private Long accountId;
    private BigDecimal available;
    private BigDecimal amount;
    private String method;
    private String wallet;
    private String password;
    private BalanceMarketDto.Quote quote;
    private BalanceMarketDto.Session session;
    private List<BalanceMarketDto.Session> active = List.of();
    private String failureCode;
    private String failureMessage;
    private volatile Result result;
    private volatile boolean processing;

    public void eraseSecrets() { setPassword(null); }

    public static boolean expired(BalanceMarketDto.Quote quote) {
        return quote.getExpiresAt() == null
                || !quote.getExpiresAt().isAfter(LocalDateTime.now(ZoneOffset.UTC));
    }
}
