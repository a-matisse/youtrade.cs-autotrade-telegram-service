package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.edit;

import cs.youtrade.autotrade.client.util.autotrade.TdpField;
import lombok.Data;

@Data
public class ScoringEditData {
    private long profitId;
    private TdpField field;
    private String value;
}
