package cs.youtrade.autotrade.client.util.autotrade.util;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import lombok.Data;

@Data
public class YouTradePurchasedHistoryDto {
    private Long tokenId;
    private String steamToken;
    private String givenName;
    private String boughtAt;
    private String itemName;
    private MarketType boughtOn;
    private Double buyPrice;
}
