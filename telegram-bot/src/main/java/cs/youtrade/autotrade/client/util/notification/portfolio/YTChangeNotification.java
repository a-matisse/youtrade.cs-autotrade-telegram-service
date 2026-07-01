package cs.youtrade.autotrade.client.util.notification.portfolio;

import cs.youtrade.autotrade.client.util.notification.YTBaseNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTChangeNotification extends YTBaseNotification {
    private String tokenName;
    private List<YTChangeItemAns> list;
}
