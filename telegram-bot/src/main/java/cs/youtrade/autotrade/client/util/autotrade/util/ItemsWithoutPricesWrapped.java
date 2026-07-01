package cs.youtrade.autotrade.client.util.autotrade.util;

import cs.youtrade.autotrade.client.util.autotrade.util.parent.ErrorMessageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ItemsWithoutPricesWrapped extends ErrorMessageDto {
    private List<ItemWithoutPrices> tmItemsWithoutPrices;
    private List<ItemWithoutPrices> waxpeerItemsWithoutPrices;

    @Data
    @AllArgsConstructor
    public static class ItemWithoutPrices {
        private String id;
        private String marketHashName;
    }
}
