package cs.youtrade.autotrade.client.util.autotrade.util;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Data
public class YouTradeSoldItemMainInfoDto {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private Long tokenId;
    private String steamToken;
    private String givenName;
    private String soldAt;
    private String itemName;
    private MarketType boughtOn;
    private MarketType soldOn;
    private Double buyPrice;
    private Double cleanSellPrice;
    private Double cleanSellPercent;

    public YouTradeSoldItemMainInfoDto(
            Long tokenId,
            String steamToken,
            String givenName,
            LocalDateTime soldAt,
            String itemName,
            MarketType boughtOn,
            Double buyPrice,
            Double cleanSellPrice
    ) {
        this.tokenId = tokenId;
        this.steamToken = steamToken;
        this.givenName = givenName;
        this.soldAt = soldAt.format(FORMATTER);
        this.itemName = itemName;
        this.boughtOn = boughtOn;
        this.soldOn = MarketType.MARKET_CSGO;
        this.buyPrice = buyPrice;
        this.cleanSellPrice = cleanSellPrice;
        this.cleanSellPercent = (cleanSellPrice * 0.95D) / buyPrice;
    }

    public String getTokenGivenName() {
        return Objects.isNull(givenName)
                ? steamToken
                : givenName;
    }
}
