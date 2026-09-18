package cs.youtrade.autotrade.client.util.notification.buy;

import cs.youtrade.autotrade.client.util.notification.BargainFailureReason;
import cs.youtrade.autotrade.client.util.notification.YTSkinNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTBargainNotification extends YTSkinNotification {
    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private String itemName;
    private BigDecimal price;
    private BigDecimal marketPrice;
    private BargainFailureReason reason;

    public BigDecimal getAdditionalProfit() {
        if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO;

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            return BigDecimal.ZERO;

        return BigDecimal.ONE
                .subtract(price.divide(marketPrice, 16, RoundingMode.HALF_UP))
                .multiply(ONE_HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
