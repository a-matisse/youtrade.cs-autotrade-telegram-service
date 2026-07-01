package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcdSellUploadGroupDto {
    private String tokenName;
    private List<FcdSellUploadDto> uploadDtos;
}
