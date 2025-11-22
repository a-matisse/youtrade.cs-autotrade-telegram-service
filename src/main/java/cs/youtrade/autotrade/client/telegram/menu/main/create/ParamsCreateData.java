package cs.youtrade.autotrade.client.telegram.menu.main.create;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParamsCreateData {
    private MarketType source;
    private MarketType destination;
}
