package cs.youtrade.autotrade.client.util.autotrade.util;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Data
@NoArgsConstructor
public class YouTradeWaitingItemMainInfoDto {
    private Long tokenId;
    private String steamToken;
    private String givenName;
    private Long assetId;
    private String itemName;
    private Double itemPrice;
    private Integer daysLeft;
    private Double curPrice;
    private Double curProfit;

    public String getTokenGivenName() {
        return Objects.isNull(givenName)
                ? steamToken
                : givenName;
    }
}
