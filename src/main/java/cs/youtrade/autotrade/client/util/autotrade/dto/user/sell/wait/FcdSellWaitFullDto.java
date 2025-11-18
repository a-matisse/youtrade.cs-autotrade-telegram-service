package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetFullCommand;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeWaitingItemMainInfoDto;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class FcdSellWaitFullDto extends AbstrFcdSellGetFullCommand<FcdSellWaitDto, YouTradeWaitingItemMainInfoDto> {
    public FcdSellWaitFullDto(
            List<FcdSellWaitDto> dtos
    ) {
        super(dtos);
    }

    public FcdSellWaitFullDto(
            String cause
    ) {
        super(cause);
    }
}
