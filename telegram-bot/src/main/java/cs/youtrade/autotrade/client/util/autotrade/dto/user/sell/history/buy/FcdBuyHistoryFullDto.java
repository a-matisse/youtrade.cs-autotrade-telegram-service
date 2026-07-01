package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.buy;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetFullCommand;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradePurchasedHistoryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FcdBuyHistoryFullDto extends AbstrFcdSellGetFullCommand<FcdBuyHistoryDto, YouTradePurchasedHistoryDto> {
}
