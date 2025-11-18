package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MarketType implements FcdDistance {
    LIS_SKINS("Lis-Skins"),
    HALOSKINS("C5.Games"),
    MARKET_CSGO("Market.CS"),
    BITSKINS("BitSkins"),
    SHADOWPAY("ShadowPay"),;

    private final String marketName;
}
