package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstractFcdPortfolioV2Data;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FcdInvV2PostGroupDto extends AbstractFcdPortfolioV2Data {
    private final List<FcdInvV2PostDto> dtos;

    public FcdInvV2PostGroupDto(String tokenName, List<FcdInvV2PostDto> dtos) {
        super(tokenName);
        this.dtos = dtos;
    }
}
