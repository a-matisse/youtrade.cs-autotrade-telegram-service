package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.inventory;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory.FcdInvV2PostGroupDto;
import lombok.Data;

import java.util.List;

@Data
public class TableV2InventoryData {
    private List<FcdInvV2PostGroupDto> dtos;
}
