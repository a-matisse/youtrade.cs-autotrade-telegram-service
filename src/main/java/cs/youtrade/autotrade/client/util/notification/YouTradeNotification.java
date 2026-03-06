package cs.youtrade.autotrade.client.util.notification;

import cs.youtrade.autotrade.client.util.minio.dto.MinIODto;
import lombok.Data;

@Data
public class YouTradeNotification {
    private long tdId;
    private long chatId;
    private String text;
    private MinIODto document;
    private MinIODto image;
}
