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
    private Long tdpId;
    private String givenName;
    private BigDecimal balance;

    private MarketType source;
    private MarketType destination;

    // Информация АВТОПОКУПКИ
    private Boolean buyWorks;
    private Double minPrice;
    private Double maxPrice;
    private Double priceFactor;
    private Set<FcdParamsGetProfitDto> profitData;
    private Integer minPopularity;
    private Integer maxPopularity;
    private Integer minDaysHold;
    private Integer maxDaysHold;
    private Double correctionCoefficient;
    private Double manipulationCoeff;
    private Double minTrendScore;
    private Double maxTrendScore;
    private Double volumeByParams;
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
        if (volumeByParams == -1)
            return "⛅ Пересчитываем объем рынка по параметрам...";
        if (volumeByParams == 0)
            return "🌧️ Сейчас нет предметов по параметрам";
        else
            return String.format("☀️ Объем рынка по параметрам: $%.2f", volumeByParams);
    }
}
