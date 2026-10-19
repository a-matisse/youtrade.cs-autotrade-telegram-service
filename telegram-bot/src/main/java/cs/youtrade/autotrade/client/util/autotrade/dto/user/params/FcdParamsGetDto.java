package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.util.autotrade.*;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
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
    private BigDecimal referralBalance;
    private FcdParamsQCData qcData;
    private Boolean bargainNotifications;
    private Boolean autoRestoreSales;

    // Информация по банку
    private BigDecimal managedFunds;
    private BigDecimal itemBalance;
    private BigDecimal buyBalance;
    private BigDecimal buyBalanceFrozen;
    private BigDecimal sellBalance;
    private BigDecimal sellBalanceFrozen;

    private MarketType source;
    private MarketType destination;

    // Информация АВТОПОКУПКИ
    private Boolean buyWorks;
    private Boolean bargainable;
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

    public String getVolumeStr(LocalizedTexts translations) {
        if (itemCountByParams == -1)
            return String.format("• %s",
                    translations.get("recalculating-market-volume-for-these-parameters"));
        if (itemCountByParams == 0)
            return String.format("• %s",
                    translations.get("no-items-match-these-parameters-right-now"));
        else
            return String.format("""
                            • %s
                            • %s: <b>$%.2f</b>""",
                            translations.format("available-items-unique", Map.of("value1", String.format("<b>%d</b>", itemCountByParams), "value2", String.format("<b>%d</b>", uniqueItemCountByParams))),
                            translations.get("volume"),
                            volumeByParams);
    }

    public String getProfileStr(UserData userData, LocalizedTexts text) {
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s: <b><code>%s</code></b>
                        • %s: <b><code>%s</code></b>
                        • %s: <b>%s</b>%s</blockquote>""",
                DynamicEmoji.PROFILE.getEmoji(), text.get("profile"),
                text.get("user-id"), tdId,
                text.get("current-params-id"), tdpId,
                text.get("route-name"), givenName,
                getBargainableStr(userData, text)
        );
    }

    private String getBargainableStr(UserData userData, LocalizedTexts text) {
        if (!isBargainAllowed(userData))
            return "";
        return bargainable
                ? String.format("\n%s: %s <b>%s</b>",
                text.get("mode"), DynamicEmoji.MARKET_BARGAINABLE.getEmoji(), text.get("bargain"))
                : String.format("\n%s: %s <b>%s</b>",
                text.get("mode"), DynamicEmoji.MARKET_MARKET.getEmoji(), text.get("market"));
    }

    public boolean isBargainAllowed(UserData userData) {
        return userData.isBargainAllowed() && source.isBargainable();
    }

    public String getQcStr(LocalizedTexts translations) {
        if (qcData == null || !qcData.isExists()) {
            return String.format("""
                            %s <b>%s</b>
                            <blockquote>%s %s</blockquote>""",
                            DynamicEmoji.FAST.getEmoji(),
                            translations.get("quickconfig-setup-2"),
                            DynamicEmoji.OFF.getEmoji(),
                            translations.get("quickconfig-disabled"));
        }

        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s: <b>%s</b>
                        • %s: <b>%s</b>
                        %s <b>%s</b></blockquote>""",
                        DynamicEmoji.FAST.getEmoji(),
                        translations.get("quickconfig-setup"),
                        translations.get("buying-threshold"),
                        translations.inBundle("parameters.TradingOptions").enumText(qcData.getBuyGrade(), "russian-name"),
                        translations.get("selling-threshold"),
                        translations.inBundle("parameters.TradingOptions").enumText(qcData.getSellGrade(), "russian-name"),
                        DynamicEmoji.ON.getEmoji(),
                        translations.get("quickconfig-enabled"));
    }

    public String getQcShortStr(LocalizedTexts translations) {
        if (qcData == null || !qcData.isExists())
            return String.format("%s %s",
                    DynamicEmoji.OFF.getEmoji(),
                    translations.get("quickconfig-disabled"));

        return String.format("%s <b>%s</b>",
                DynamicEmoji.ON.getEmoji(),
                translations.get("quickconfig-enabled"));
    }

    public String getDirection() {
        return getDirection(source, destination);
    }

    public static String getDirection(MarketType source, MarketType destination) {
        return String.format("%s → %s",
                getMarketWithLink(source),
                getMarketWithLink(destination)
        );
    }

    public static String getMarketWithLink(MarketType type) {
        return String.format("<a href=\"%s\"><b>%s</b></a>", decideLink(type), type.getMarketName());
    }

    public static String decideLink(MarketType type) {
        return switch (type) {
            case CSFLOAT -> "https://csfloat.com/";
            case LIS_SKINS -> "https://lis-skins.short.gy/mrtwister-april";
            case MARKET_CSGO ->
                    "https://market.csgo.com/?utm_campaign=free&utm_source=youtradecs&utm_medium=telegram&cpid=21df92e0-95f3-4371-a6e3-bc20b9419289&oid=4c69d079-ad2a-44b0-a9ac-d0afc2167ee7";
            default -> "";
        };
    }
}
