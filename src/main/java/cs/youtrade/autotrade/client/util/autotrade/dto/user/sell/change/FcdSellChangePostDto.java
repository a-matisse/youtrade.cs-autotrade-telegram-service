package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
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
