package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.edit;

import cs.youtrade.autotrade.client.util.autotrade.YdpField;
import lombok.Data;

@Data
public class ScoringEditData {
    private long scoringId;
    private YdpField field;
    private String value;
}
