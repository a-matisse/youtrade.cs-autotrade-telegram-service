package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.update;

import cs.youtrade.autotrade.client.util.autotrade.TdpField;
import lombok.Data;

@Data
public class UserAutoBuyUpdateData {
    private TdpField field;
    private String value;
}
