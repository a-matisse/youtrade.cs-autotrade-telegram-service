package cs.youtrade.autotrade.client.util.notification.buy;

import cs.youtrade.autotrade.client.util.notification.YTSkinNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTBuyCompletedNotification extends YTSkinNotification {
    private String itemName;
    private BigDecimal price;
    private BigDecimal priceFactor;
    private int unlock;
    private Map<String, Double> priceMap;
    private Map<String, Double> percentMap;
    private Map<String, Double> trendMap;
    private BigDecimal balance;
}
