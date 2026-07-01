package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcdSellRestrictPostDto {
    private String tokenIdStr;
    private String assetIdStr;
    private String name;
    private String flagStr;
}
