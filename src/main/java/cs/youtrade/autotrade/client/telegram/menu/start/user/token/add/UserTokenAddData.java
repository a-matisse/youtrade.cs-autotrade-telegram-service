package cs.youtrade.autotrade.client.telegram.menu.start.user.token.add;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import lombok.Data;

@Data
public class UserTokenAddData {
    private MarketType source;
    private MarketType destination;
    private TokenChooseOption opt;
    private String api;
    private String partnerId;
    private String steamToken;

    public void setDirection (FcdParamsGetDto tdp) {
        this.source = tdp.getSource();
        this.destination = tdp.getDestination();
    }
}
