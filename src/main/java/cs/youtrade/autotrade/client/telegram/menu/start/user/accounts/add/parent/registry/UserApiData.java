package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import lombok.Data;

@Data
public class UserApiData {
    private MarketType source;
    private MarketType destination;
    private String api;
    private String partnerId;
    private String steamToken;

    public void setDirection (FcdParamsGetDto tdp) {
        this.source = tdp.getSource();
        this.destination = tdp.getDestination();
    }
}
