package cs.youtrade.autotrade.client.util.autotrade.util;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class YouTradeOnSellItemMainInfoDto {
    private Long tokenId;
    private String steamToken;
    private String givenName;
    private Long youTradeId;
    private LocalDateTime purchasedAt;
    private String itemName;
    private Double itemPrice;
    private Double itemMin;
    private Double itemMax;
}
