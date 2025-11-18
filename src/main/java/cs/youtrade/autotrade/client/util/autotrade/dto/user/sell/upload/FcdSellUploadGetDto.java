package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload;

import cs.youtrade.autotrade.client.util.autotrade.util.ItemsWithoutPricesWrapped;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FcdSellUploadGetDto {
    private String tokenName;
    private List<ItemsWithoutPricesWrapped.ItemWithoutPrices> inv;
}
