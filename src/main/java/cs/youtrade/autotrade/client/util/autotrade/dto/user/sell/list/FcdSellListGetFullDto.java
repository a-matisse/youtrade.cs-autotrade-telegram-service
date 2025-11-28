package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetFullCommand;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeOnSellItemMainInfoDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FcdSellListGetFullDto extends AbstrFcdSellGetFullCommand<FcdSellListGetDto, YouTradeOnSellItemMainInfoDto> {
}
