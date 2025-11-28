package cs.youtrade.autotrade.client.util.autotrade.dto.norole;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdGetPricesDto extends AbstractFcdDto {
    private Map<MarketType, BigDecimal> buySubPrices;
    private Map<MarketType, BigDecimal> sellSubPrices;
    private BigDecimal currency;
}
