package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetSingleCommand;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeSoldItemMainInfoDto;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class FcdSellHistoryDto extends AbstrFcdSellGetSingleCommand<YouTradeSoldItemMainInfoDto> {
    public FcdSellHistoryDto(
            List<YouTradeSoldItemMainInfoDto> onSellList,
            String tokenName,
            Long tmTokenId
    ) {
        super(onSellList, tokenName, tmTokenId);
    }
}
