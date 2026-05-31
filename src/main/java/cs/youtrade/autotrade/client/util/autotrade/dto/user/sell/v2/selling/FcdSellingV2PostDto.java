package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.selling;

import lombok.Data;

@Data
public class FcdSellingV2PostDto {
    private String idStr;
    private String name;
    private String marketPriceStr;
    private String oldMinStr;
    private String oldMaxStr;
    private String oldBaseStr;
    private String newMinStr;
    private String newMaxStr;
    private String newBaseStr;
    private String flagStr;
}
