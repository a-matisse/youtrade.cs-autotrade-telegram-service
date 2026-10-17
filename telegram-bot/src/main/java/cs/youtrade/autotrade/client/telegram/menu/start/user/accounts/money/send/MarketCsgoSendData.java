package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send;

import lombok.Getter;
import lombok.Setter;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdMarketCsgoMoneySendDto;

import java.math.BigDecimal;

@Getter
@Setter
public class MarketCsgoSendData {
    public enum Result { TEST_UNKNOWN, TEST_FAILED, NOT_RECEIVED, REMAINDER_UNKNOWN, REMAINDER_FAILED, COMPLETED }

    private Long tokenId;
    private BigDecimal available;
    private String password;
    private String destinationKey;
    private boolean passwordProvided;
    private boolean destinationProvided;
    private BigDecimal amount;
    private volatile Result result;
    private FcdMarketCsgoMoneySendDto response;
    private BigDecimal currentBalance;
    private volatile boolean processing;
    private volatile boolean testSent;
    private volatile boolean remainderAttempted;

    public void eraseSecrets() {
        setPassword(null);
        setDestinationKey(null);
    }
}
