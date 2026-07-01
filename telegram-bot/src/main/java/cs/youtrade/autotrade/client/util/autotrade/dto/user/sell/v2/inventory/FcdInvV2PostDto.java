package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FcdInvV2PostDto {
    private String assetId;
    private String itemName;
    // Поля для выставления на продажу
    private String boughtStr;
    private String minStr;
    private String maxStr;
    // Поля для запрета продажи
    private String restrictStr;
}
