package cs.youtrade.autotrade.client.telegram.menu.start.user.params.table.change;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change.FcdSellChangePostDto;
import lombok.Data;

import java.util.List;

@Data
public class TableChangeData {
    private TableChangeType type;
    private List<FcdSellChangePostDto> dtos;
}
