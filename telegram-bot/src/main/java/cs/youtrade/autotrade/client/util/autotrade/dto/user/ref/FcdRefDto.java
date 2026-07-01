package cs.youtrade.autotrade.client.util.autotrade.dto.user.ref;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class FcdRefDto {
    private BigDecimal turnover;
    private BigDecimal discount;
    private String thisRef;
    private String usedRef;
    private BigDecimal refRate;
    private BigDecimal refReward;
}
