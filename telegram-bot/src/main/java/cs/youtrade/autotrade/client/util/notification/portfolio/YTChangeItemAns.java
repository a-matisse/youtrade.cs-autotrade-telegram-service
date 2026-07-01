package cs.youtrade.autotrade.client.util.notification.portfolio;

import lombok.Data;

@Data
public class YTChangeItemAns {
    private Long assetId;
    private String itemName;
    private Double newMin;
    private Double newMax;
    private Double newBase;
}
