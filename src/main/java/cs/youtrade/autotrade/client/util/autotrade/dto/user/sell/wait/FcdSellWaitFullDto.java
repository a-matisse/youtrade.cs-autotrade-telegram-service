package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetFullCommand;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeWaitingItemMainInfoDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FcdSellWaitFullDto extends AbstrFcdSellGetFullCommand<FcdSellWaitDto, YouTradeWaitingItemMainInfoDto> {
}
