package cs.youtrade.autotrade.client.util.autotrade.dto.user.general;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class DepositBonusProgressDto {
    private BigDecimal turnover;
    private BigDecimal currentBonusRate;
    private Boolean locked;
    private DepositBonusTierDto nextTier;
}
