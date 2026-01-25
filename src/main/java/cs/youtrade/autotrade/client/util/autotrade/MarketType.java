package cs.youtrade.autotrade.client.util.autotrade;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum MarketType implements FcdDistance, IMenuEnum {
    LIS_SKINS("Lis-Skins", true, false, false),
    HALOSKINS("C5.Games", true, false, false),
    MARKET_CSGO("Market.CS", false, true, false),
    BITSKINS("BitSkins", false, false, false),
    SHADOWPAY("ShadowPay", false, false, false),
    STEAM("Steam", false, true, false),
    CSFLOAT("CSFloat", true, false, false),
    DM("DMarket", false, true, false);

    private final String marketName;
    private final boolean autobuy;
    private final boolean autosell;
    private final boolean parse;

    @Override
    public String getButtonName() {
        return marketName;
    }

    @Override
    public int getRowNum() {
        return this.ordinal() / 2;
    }
}
