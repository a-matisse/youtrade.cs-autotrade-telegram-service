package cs.youtrade.autotrade.client.telegram.menu.main.create;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import lombok.Data;

@Data
public class ParamsCreateData {
    private MarketType source;
    private MarketType destination;
}
