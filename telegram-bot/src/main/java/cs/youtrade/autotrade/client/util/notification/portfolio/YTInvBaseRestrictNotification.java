package cs.youtrade.autotrade.client.util.notification.portfolio;

import cs.youtrade.autotrade.client.util.notification.YTBaseNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTInvBaseRestrictNotification extends YTBaseNotification {
    private String tokenName;
    private int amount;
}
