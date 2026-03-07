package cs.youtrade.autotrade.client.util.autotrade.util;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.excel.ExcelExclude;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class YouTradePurchasedHistoryDto {
    @ExcelExclude
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private Long tokenId;
    private String steamToken;
    private String givenName;
    private String boughtAt;
    private String itemName;
    private MarketType boughtOn;
    private Double buyPrice;
}
