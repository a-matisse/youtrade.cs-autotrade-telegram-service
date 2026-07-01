package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.selling;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstractFcdPortfolioV2Data;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FcdSellingV2PostGroupDto extends AbstractFcdPortfolioV2Data {
    private List<FcdSellingV2PostDto> dtos;

    public FcdSellingV2PostGroupDto(String tokenName, List<FcdSellingV2PostDto> dtos) {
        super(tokenName);
        this.dtos = dtos;
    }
}
