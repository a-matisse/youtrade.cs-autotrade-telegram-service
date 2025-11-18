package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class FcdSellRestrictPostDto {
    private String tokenIdStr;
    private String assetIdStr;
    private String name;
    private String flagStr;
}
