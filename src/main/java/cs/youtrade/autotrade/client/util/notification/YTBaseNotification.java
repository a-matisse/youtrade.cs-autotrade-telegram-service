package cs.youtrade.autotrade.client.util.notification;

import lombok.Data;

@Data
public abstract class YTBaseNotification {
    private YTNotificationType type;
    private long tdId;
    private long chatId;
}
