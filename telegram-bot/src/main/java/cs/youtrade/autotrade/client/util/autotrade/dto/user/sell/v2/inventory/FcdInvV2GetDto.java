package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory;

import lombok.Data;

import java.util.List;

@Data
public class FcdInvV2GetDto {
    private String tokenName;
    private Long tmTokenId;
    private List<FcdInvV2ItemDto> items;
}
