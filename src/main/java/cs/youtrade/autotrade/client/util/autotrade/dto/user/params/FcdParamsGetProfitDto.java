package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.util.autotrade.ItemScoringType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FcdParamsGetProfitDto {
    private Long profitId;
    private ItemScoringType scoringType;
    private Double minProfit;
    private Integer period;
}
