package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict;

import cs.youtrade.autotrade.client.util.autotrade.util.ItemsWithoutPricesWrapped;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FcdSellRestrictGetDto {
    private String tokenName;
    private Long tmTokenId;
    private List<ItemsWithoutPricesWrapped.ItemWithoutPrices> items;
}
