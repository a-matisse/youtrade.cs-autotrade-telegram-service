package cs.youtrade.autotrade.client.util.autotrade.dto.user.general;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class DepositBonusTierDto {
    private BigDecimal turnoverThreshold;
    private BigDecimal bonusRate;
    private BigDecimal remainingTurnover;
}
