package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change;

import cs.youtrade.autotrade.client.util.autotrade.util.PriceIntervalUpdateV2Dto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FcdSellChangeGetDto {
    private String tokenName;
    private List<PriceIntervalUpdateV2Dto> changeList;
}
