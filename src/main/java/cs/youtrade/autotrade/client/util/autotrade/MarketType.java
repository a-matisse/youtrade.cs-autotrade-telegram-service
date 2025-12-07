package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum MarketType implements FcdDistance {
    LIS_SKINS("Lis-Skins", true, false, false),
    HALOSKINS("C5.Games", false, false, false),
    MARKET_CSGO("Market.CS", false, true, false),
    BITSKINS("BitSkins", false, false, false),
    SHADOWPAY("ShadowPay", false, false, false);

    private final String marketName;
    private final boolean autobuy;
    private final boolean autosell;
    private final boolean parse;

    private boolean getAvailable() {
        return autobuy || autosell || parse;
    }

    private String getAllows() {
        List<String> allows = new ArrayList<>();
        if (autobuy) allows.add("покупка");
        if (autosell) allows.add("продажа");
        if (parse) allows.add("парсинг");
        return String.join(", ", allows);
    }

    public static String generateDescription() {
        return Arrays
                .stream(values())
                .filter(MarketType::getAvailable)
                .map(val -> String.format(
                        "<code>%s</code> - %s", val.name(), val.getAllows()))
                .collect(Collectors.joining("\n"));
    }
}
