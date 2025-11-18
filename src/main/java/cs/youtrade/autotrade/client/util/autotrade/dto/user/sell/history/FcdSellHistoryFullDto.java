package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetFullCommand;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeSoldItemMainInfoDto;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class FcdSellHistoryFullDto extends AbstrFcdSellGetFullCommand<FcdSellHistoryDto, YouTradeSoldItemMainInfoDto> {
    public FcdSellHistoryFullDto(
            List<FcdSellHistoryDto> dtos
    ) {
        super(dtos);
    }

    public FcdSellHistoryFullDto(
            String cause
    ) {
        super(cause);
    }
}
