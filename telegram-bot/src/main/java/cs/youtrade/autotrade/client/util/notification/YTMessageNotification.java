package cs.youtrade.autotrade.client.util.notification;

import cs.youtrade.autotrade.client.util.minio.dto.MinIODto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTMessageNotification extends YTBaseNotification {
    private String text;
    private MinIODto document;
    private MinIODto image;
}
