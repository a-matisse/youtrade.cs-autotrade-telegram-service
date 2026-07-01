package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcdSellListPostDto {
    private String tokenIdStr;
    private String ytIdStr;
    private String flagStr;
}
