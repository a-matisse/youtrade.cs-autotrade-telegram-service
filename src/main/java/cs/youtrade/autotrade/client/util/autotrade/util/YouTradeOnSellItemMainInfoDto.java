package cs.youtrade.autotrade.client.util.autotrade.util;

import lombok.Data;

import java.util.Objects;

@Data
public class YouTradeOnSellItemMainInfoDto {
    private Long tokenId;
    private String steamToken;
    private String givenName;
    private Long youTradeId;
    private String itemName;
    private Double itemPrice;
    private Double itemMin;
    private Double itemMax;

    public YouTradeOnSellItemMainInfoDto(
            Long tokenId,
            String steamToken,
            String givenName,
            Long youTradeId,
            String itemName,
            Double itemPrice,
            Double itemMin,
            Double itemMax
    ) {
        this.tokenId = tokenId;
        this.steamToken = steamToken;
        this.givenName = givenName;
        this.youTradeId = youTradeId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemMin = itemMin;
        this.itemMax = itemMax;
    }

    public String getTokenGivenName() {
        return Objects.isNull(givenName)
                ? steamToken
                : givenName;
    }
}
