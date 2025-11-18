package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FcdSellUploadGroupDto {
    private String tokenName;
    private List<FcdSellUploadDto> uploadDtos;
}
