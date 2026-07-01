package cs.youtrade.autotrade.client.util.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTBalanceNotification extends YTBaseNotification {
    private BigDecimal balance;
}
