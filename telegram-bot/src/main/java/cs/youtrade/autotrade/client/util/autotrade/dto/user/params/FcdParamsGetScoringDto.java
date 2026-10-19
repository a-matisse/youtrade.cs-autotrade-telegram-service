package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
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

    public String asMessage(LocalizedTexts translations) {
        return String.format("""
                        ID=<code>%d</code> — <b>%s</b>
                        • <i>%s</i>: <b>%s</b> | <i>%s</i>: <b>%.2f%%</b>
                        • <i>%s</i>: <b>%.2f%%</b> → <b>%.2f%%</b>
                        """,
                        scoringId,
                        translations.inBundle("parameters.TradingOptions").enumText(scoringType, "russian-name"),
                        translations.get("period"), getPeriodStr(translations),
                        translations.get("minimum-return"), minProfit * 100d,
                        translations.get("trend"),
                        minTrendScore * 100,
                        maxTrendScore * 100);
    }

    private String getPeriodStr(LocalizedTexts translations) {
        return String.format("%d %s",
                period, (period == 1 ? translations.get("day") : translations.get("days")));
    }
}
