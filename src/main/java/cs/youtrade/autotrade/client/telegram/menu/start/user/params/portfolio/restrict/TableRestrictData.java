package cs.youtrade.autotrade.client.telegram.menu.start.user.params.portfolio.restrict;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict.FcdSellRestrictPostDto;
import lombok.Data;

import java.util.List;

@Data
public class TableRestrictData {
    private List<FcdSellRestrictPostDto> dtos;
}
