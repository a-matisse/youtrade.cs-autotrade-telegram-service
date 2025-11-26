package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.selling;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListPostDto;
import lombok.Data;

import java.util.List;

@Data
public class TableSellingData {
    private List<FcdSellListPostDto> dtos;
}
