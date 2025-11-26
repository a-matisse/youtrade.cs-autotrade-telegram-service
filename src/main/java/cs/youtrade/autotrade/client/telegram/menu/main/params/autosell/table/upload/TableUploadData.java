package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.upload;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadGroupDto;
import lombok.Data;

import java.util.List;

@Data
public class TableUploadData {
    private List<FcdSellUploadGroupDto> dtos;
}
