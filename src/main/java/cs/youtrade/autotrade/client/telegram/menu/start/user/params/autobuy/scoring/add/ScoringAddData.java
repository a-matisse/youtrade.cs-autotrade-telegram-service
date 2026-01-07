package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring.add;

import cs.youtrade.autotrade.client.util.autotrade.ItemScoringType;
import lombok.Data;

@Data
public class ScoringAddData {
    private ItemScoringType type;
    private Double minProfit;
}
