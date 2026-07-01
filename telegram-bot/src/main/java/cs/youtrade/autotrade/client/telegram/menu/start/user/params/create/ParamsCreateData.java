package cs.youtrade.autotrade.client.telegram.menu.start.user.params.create;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import lombok.Data;

@Data
public class ParamsCreateData {
    private MarketType source;
    private MarketType destination;
}
