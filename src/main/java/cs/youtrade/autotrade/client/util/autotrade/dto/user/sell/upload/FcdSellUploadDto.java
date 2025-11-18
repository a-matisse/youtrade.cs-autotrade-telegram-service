package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class FcdSellUploadDto {
    private String assetId;
    private String name;
    private String minStr;
    private String maxStr;
    private String boughtStr;
}
