package cs.youtrade.autotrade.client.util.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTPaymentNotification extends YTBaseNotification {
    private Boolean successful;
    private BigDecimal amount;
    private String currency;
    private String idempotencyKey;
    private String topUpType;
    private String paymentTime;
}
