package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FcdSellListPostDto {
    private String tokenIdStr;
    private String ytIdStr;
    private String flagStr;
}
