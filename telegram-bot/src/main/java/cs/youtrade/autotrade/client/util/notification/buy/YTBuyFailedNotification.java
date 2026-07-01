package cs.youtrade.autotrade.client.util.notification.buy;

import cs.youtrade.autotrade.client.util.notification.YTSkinNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTBuyFailedNotification extends YTSkinNotification {
    private String itemName;
    private BigDecimal refunded;
    private String reason;
}
