package cs.youtrade.autotrade.client.util.notification.portfolio;

import cs.youtrade.autotrade.client.util.autotrade.dto.DeleteAnsDto;
import cs.youtrade.autotrade.client.util.notification.YTBaseNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YTDeleteNotification extends YTBaseNotification {
    private DeleteAnsDto ans;
}
