package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum MarketType implements FcdDistance {
    LIS_SKINS("Lis-Skins", true, false, false),
    HALOSKINS("C5.Games", true, false, false),
    MARKET_CSGO("Market.CS", false, true, false),
    BITSKINS("BitSkins", false, false, false),
    SHADOWPAY("ShadowPay", false, false, false),
    STEAM("Steam", false, false, false),
    CSFLOAT("CSFloat", true, false, false),
    DM("DMarket", false, true, false);

    private static final List<MarketType> autoBuyList;
    private static final List<MarketType> autoSellList;
    private static final List<MarketType> parseList;

    static {
        autoBuyList = getList(MarketType::isAutobuy);
        autoSellList = getList(MarketType::isAutosell);
        parseList = getList(MarketType::isParse);
    }

    private final String marketName;
    private final boolean autobuy;
    private final boolean autosell;
    private final boolean parse;

    private boolean getAvailable() {
        return autobuy || autosell || parse;
    }

    private String getAllows() {
        List<String> allows = new ArrayList<>();
        if (autobuy)
            allows.add("покупка");
        if (autosell)
            allows.add("продажа");
        if (parse)
            allows.add("парсинг");
        return String.join(", ", allows);
    }

    public static String genAutoBuyDesc() {
        return generateDescription(autoBuyList);
    }

    public static String genAutoSellDesc() {
        return generateDescription(autoSellList);
    }

    public static String genParseDesc() {
        return generateDescription(parseList);
    }

    private static String generateDescription(List<MarketType> marketTypes) {
        return Arrays
                .stream(values())
                .filter(MarketType::getAvailable)
                .map(val -> String.format(
                        "<code>%s</code> - %s", val.name(), val.getAllows()))
                .collect(Collectors.joining("\n"));
    }

    private static List<MarketType> getList(Predicate<MarketType> p) {
        return Arrays.stream(values()).filter(p).toList();
    }
}
