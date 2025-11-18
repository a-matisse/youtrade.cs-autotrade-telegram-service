package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetFullCommand;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeOnSellItemMainInfoDto;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class FcdSellListGetFullDto extends AbstrFcdSellGetFullCommand<FcdSellListGetDto, YouTradeOnSellItemMainInfoDto> {
    public FcdSellListGetFullDto(
            List<FcdSellListGetDto> dtos
    ) {
        super(dtos);
    }

    public FcdSellListGetFullDto(
            String cause
    ) {
        super(cause);
    }
}
