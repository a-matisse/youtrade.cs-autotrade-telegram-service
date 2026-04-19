package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.util.autotrade.*;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
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
    private FcdParamsQCData qcData;

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

    public String getProfileStr() {
        return String.format("""
                        %s <b>Профиль</b>
                        <blockquote>• ID пользователя: <b><code>%s</code></b>
                        • Текущий params-ID: <b><code>%s</code></b>
                        • Имя маршрута: <b>%s</b></blockquote>""",
                DynamicEmoji.PROFILE.getEmoji(),
                tdId,
                tdpId,
                givenName
        );
    }

    public String getQcStr() {
        if (qcData == null || !qcData.isExists())
            return String.format("%s Быстрая настройка выключена",
                    DynamicEmoji.OFF.getEmoji());
        return String.format("""
                        %s <b>Быстрая настройка включена</b>
                        <blockquote>• Порог покупки: <b>%s</b>
                        • Порог продажи: <b>%s</b></blockquote>""",
                DynamicEmoji.ON.getEmoji(),
                qcData.getBuyGrade().getRussianName(),
                qcData.getSellGrade().getRussianName()
        );
    }

    public String getQcShortStr() {
        if (qcData == null || !qcData.isExists())
            return String.format("%s Быстрая настройка выключена",
                    DynamicEmoji.OFF.getEmoji());

        return String.format("%s <b>Быстрая настройка включена</b>",
                DynamicEmoji.ON.getEmoji());
    }

    public String getDirection() {
        return getDirection(source, destination);
    }

    public static String getDirection(MarketType source, MarketType destination) {
        return String.format("""
                        <a href="%s"><b>%s</b></a> → <a href="%s"><b>%s</b></a>""",
                decideLink(source), source.getMarketName(),
                decideLink(destination), destination.getMarketName()
        );
    }

    public static String decideLink(MarketType type) {
        return switch (type) {
            case CSFLOAT -> "https://csfloat.com/";
            case LIS_SKINS -> "https://lis-skins.short.gy/mrtwister-april";
            case MARKET_CSGO -> "https://market.csgo.com/?utm_campaign=free&utm_source=youtradecs&utm_medium=telegram&cpid=21df92e0-95f3-4371-a6e3-bc20b9419289&oid=4c69d079-ad2a-44b0-a9ac-d0afc2167ee7";
            default -> "";
        };
    }
}
