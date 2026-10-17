package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect;

import lombok.Getter;
import lombok.Setter;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdMarketCsgoBalanceTransferDto;

@Getter
@Setter
public class MarketCsgoCollectData {
    private Long tokenId;
    private FcdMarketCsgoBalanceTransferDto response;
    private volatile boolean completed;
    private volatile boolean processing;
}
