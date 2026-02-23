package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.util.autotrade.DuplicateMode;
import cs.youtrade.autotrade.client.util.autotrade.FunctionType;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.SellPriceEvalMode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class FcdParamsGetDto {
    // Основная информация
    private Long tdId;
    private Long tdpId;
    private String givenName;
    private BigDecimal balance;
    private Boolean configExists;

    private MarketType source;
    private MarketType destination;

    // Информация АВТОПОКУПКИ
    private Boolean buyWorks;
    private Double minPrice;
    private Double maxPrice;
    private Double priceFactor;
    private Set<FcdParamsGetScoringDto> scoringData;
    private Integer minPopularity;
    private Integer maxPopularity;
    private Integer minDaysHold;
    private Integer maxDaysHold;
    private Double correctionCoefficient;
    private Double manipulationCoeff;
    private Double volumeByParams;
    private Integer uniqueItemCountByParams;
    private Integer itemCountByParams;
    private Double maxDuplicates;
    private Integer duplicateLag;
    private FunctionType functionType;
    private DuplicateMode duplicateMode;

    // Информация АВТОПРОДАЖИ
    private Boolean sellWorks;
    private Double minSellProfit;
    private Double maxSellProfit;
    private SellPriceEvalMode evalMode;
    private Integer evalModeC1;
    private Boolean evalModeS1;

    // Предлагаемый коэффициент evalModeC1
    private Integer suggEvalModeC1;

    // Список следования (синхронизации)
    private List<FcdParamsFollowDto> follows;

    public String getVolumeStr() {
        if (itemCountByParams == -1)
            return "• Пересчитываем объем рынка по параметрам...";
        if (itemCountByParams == 0)
            return "• Сейчас нет предметов по параметрам";
        else
            return String.format("""
                    • Доступно <b>%d</b> предметов (<b>%d</b> уникальных)
                    • Объём: <b>$%.2f</b>""",
                    itemCountByParams,
                    uniqueItemCountByParams,
                    volumeByParams
            );
    }
}
