package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcdSellChangePostDto {
    private String idStr;
    private String name;
    private String marketPriceStr;
    private String oldMinStr;
    private String oldMaxStr;
    private String oldBaseStr;
    private String newMinStr;
    private String newMaxStr;
    private String newBaseStr;
}
