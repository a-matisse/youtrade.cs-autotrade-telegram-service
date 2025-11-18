package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetSingleCommand;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeOnSellItemMainInfoDto;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class FcdSellListGetDto extends AbstrFcdSellGetSingleCommand<YouTradeOnSellItemMainInfoDto> {
    public FcdSellListGetDto(
            List<YouTradeOnSellItemMainInfoDto> onSellList,
            String tokenName,
            Long tmTokenId
    ) {
        super(onSellList, tokenName, tmTokenId);
    }
}
