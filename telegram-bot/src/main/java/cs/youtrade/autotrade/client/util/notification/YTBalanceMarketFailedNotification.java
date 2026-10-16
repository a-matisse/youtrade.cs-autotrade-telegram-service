package cs.youtrade.autotrade.client.util.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class YTBalanceMarketFailedNotification extends YTBaseNotification {
    private Long sessionId;
    private Long steamTokenId;
    private BigDecimal sourceAmount;
    private String withdrawalMethod;
    private String reason;
    private String failedAt;
}
