package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetFullCommand;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeSoldItemMainInfoDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FcdSellHistoryFullDto extends AbstrFcdSellGetFullCommand<FcdSellHistoryDto, YouTradeSoldItemMainInfoDto> {
}
