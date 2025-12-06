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
    private Double minTrendScore;
    private Double maxTrendScore;

    public String asMessage() {
        return String.format("""
                        #%d %s
                        ⏱️ Период: %s | 💰 Мин. профит: %.2f%%
                        📊 Диапазон тренда: %.2f%% → %.2f%%
                        """,
                profitId,
                scoringType,
                period,
                minProfit * 100d,
                minTrendScore * 100,
                maxTrendScore * 100
        );
    }
}
