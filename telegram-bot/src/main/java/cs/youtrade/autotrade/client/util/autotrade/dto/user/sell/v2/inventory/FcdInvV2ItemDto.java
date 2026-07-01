package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory;

import lombok.Data;

@Data
public class FcdInvV2ItemDto {
    private Long assetId;
    private String itemName;
    private boolean restricted;
}
