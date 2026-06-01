package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.selling.FcdSellingV2PostGroupDto;
import lombok.Data;

import java.util.List;

@Data
public class TableV2SellingData {
    private List<FcdSellingV2PostGroupDto> dtos;
}
