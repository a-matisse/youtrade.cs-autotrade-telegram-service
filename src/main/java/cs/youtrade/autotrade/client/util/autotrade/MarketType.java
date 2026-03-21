package cs.youtrade.autotrade.client.util.autotrade;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum MarketType implements FcdDistance, IMenuEnum {
    LIS_SKINS("Lis-Skins", true, false, false),
    HALOSKINS("C5.Games", false, false, false),
    MARKET_CSGO("Market.CS", false, true, false),
    BITSKINS("BitSkins", false, false, false),
    SHADOWPAY("ShadowPay", false, false, false),
    STEAM("Steam", false, false, false),
    CSFLOAT("CSFloat", true, false, false),
    DM("DMarket", false, false, false),
    RETURN("↩️ Назад");

    public static final List<MarketType> BUY_DIRS = getMarketTypes(MarketType::isAutobuy);
    public static final List<MarketType> SELL_DIRS = getMarketTypes(MarketType::isAutosell);

    private final String optionName;
    private final String marketName;
    private final boolean autobuy;
    private final boolean autosell;
    private final boolean parse;

    MarketType(
            String marketName
    ) {
        this.optionName = name();
        this.marketName = marketName;
        this.autobuy = true;
        this.autosell = true;
        this.parse = true;
    }

    MarketType(
            String marketName,
            boolean autobuy,
            boolean autosell,
            boolean parse
    ) {
        this.optionName = name();
        this.marketName = marketName;
        this.autobuy = autobuy;
        this.autosell = autosell;
        this.parse = parse;
    }

    private static List<MarketType> getMarketTypes(Predicate<MarketType> predicate) {
        return Arrays.stream(MarketType.values()).filter(predicate).collect(Collectors.toList());
    }

    @Override
    public String getButtonName() {
        return marketName;
    }

    @Override
    public int getRowNum() {
        return this.equals(RETURN)
                ? this.ordinal()
                : this.ordinal() / 2;
    }
}
