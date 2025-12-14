package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.update;

import cs.youtrade.autotrade.client.util.autotrade.TdpField;
import lombok.Data;

@Data
public class UserAutoSellUpdateData {
    private TdpField field;
    private String value;
}
