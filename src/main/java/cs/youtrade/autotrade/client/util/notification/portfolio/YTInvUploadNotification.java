package cs.youtrade.autotrade.client.util.notification.portfolio;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadInfoDto;
import cs.youtrade.autotrade.client.util.notification.YTBaseNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTInvUploadNotification extends YTBaseNotification {
    private boolean success;
    private FcdSellUploadInfoDto info;
}