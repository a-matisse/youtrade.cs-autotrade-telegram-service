package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.util.autotrade.ItemScoringType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FcdParamsGetScoringDto {
    private Long scoringId;
    private ItemScoringType scoringType;
    private Double minProfit;
    private Integer period;
    private Double minTrendScore;
    private Double maxTrendScore;

    public String asMessage() {
        return String.format("""
                        ID=<code>%d</code> — <b>%s</b>
                        • <i>Период</i>: <b>%s</b> | <i>Прибыль от</i>: <b>%.2f%%</b>
                        • <i>Тренд</i>: <b>%.2f%%</b> → <b>%.2f%%</b>
                        """,
                scoringId,
                scoringType.getRussianName(),
                getPeriodStr(),
                minProfit * 100d,
                minTrendScore * 100,
                maxTrendScore * 100
        );
    }

    private String getPeriodStr() {
        return String.format("%d %s",
                period, (period == 1 ? "день" : "дн."));
    }
}
