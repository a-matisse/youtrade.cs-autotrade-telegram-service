package cs.youtrade.autotrade.client.util.notification.sell;

import cs.youtrade.autotrade.client.util.notification.YTSkinNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTSellAddedNotification extends YTSkinNotification {
    private String itemName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal buyPrice;
    private String boughtAt;
}
