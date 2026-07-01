package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait;


import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetSingleCommand;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeWaitingItemMainInfoDto;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class FcdSellWaitDto extends AbstrFcdSellGetSingleCommand<YouTradeWaitingItemMainInfoDto> {
    public FcdSellWaitDto(
            List<YouTradeWaitingItemMainInfoDto> onSellList,
            String tokenName,
            Long tmTokenId
    ) {
        super(onSellList, tokenName, tmTokenId);
    }
}
