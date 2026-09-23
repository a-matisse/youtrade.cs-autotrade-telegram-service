package cs.youtrade.autotrade.client.util.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class YTAccessNotification extends YTBaseNotification {
    private String effectiveAt;
    private String until;
    private String reason;
}
