package cs.youtrade.autotrade.client.util.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class YTBalanceMarketPayoutNotification extends YTBaseNotification {
    private Long sessionId;
    private Long steamTokenId;
    private BigDecimal sourceAmount;
    private BigDecimal payoutAmount;
    private String payoutCurrency;
    private String withdrawalMethod;
    private String walletAddress;
    private String redBalanceOrderId;
    private String payoutId;
    private String payoutTxid;
    private String paidAt;
}
