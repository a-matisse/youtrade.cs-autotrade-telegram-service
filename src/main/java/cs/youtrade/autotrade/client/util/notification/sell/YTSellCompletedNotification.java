package cs.youtrade.autotrade.client.util.notification.sell;

import cs.youtrade.autotrade.client.util.notification.YTSkinNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTSellCompletedNotification extends YTSkinNotification {
    private String itemName;
    private BigDecimal profit;
    private BigDecimal boughtFor;
    private BigDecimal soldFor;
    private String purchasedAt;
}
