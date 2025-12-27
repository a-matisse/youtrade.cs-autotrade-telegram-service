package cs.youtrade.autotrade.client.util.autotrade.util;

import cs.youtrade.autotrade.client.util.excel.ExcelExclude;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class YouTradeSoldItemMainInfoDto {
    @ExcelExclude
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

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
