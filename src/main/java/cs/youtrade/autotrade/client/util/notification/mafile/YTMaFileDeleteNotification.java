package cs.youtrade.autotrade.client.util.notification.mafile;

import cs.youtrade.autotrade.client.util.notification.YTBaseNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTMaFileDeleteNotification extends YTBaseNotification {
    private Long tokenId;
    private String givenName;
    private String reason;
}