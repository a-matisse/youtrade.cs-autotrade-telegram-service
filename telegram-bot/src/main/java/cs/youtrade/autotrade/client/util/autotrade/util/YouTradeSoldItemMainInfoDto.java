package cs.youtrade.autotrade.client.util.autotrade.util;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import lombok.Data;

@Data
public class YouTradeSoldItemMainInfoDto {
    private Long tokenId;
    private String steamToken;
    private String givenName;
    private String boughtAt;
    private String soldAt;
    private String itemName;
    private MarketType boughtOn;
    private MarketType soldOn;
    private Double buyPrice;
    private Double cleanSellPrice;
    private Double cleanSellPercent;
}
