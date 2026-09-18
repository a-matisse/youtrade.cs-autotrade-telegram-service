package cs.youtrade.autotrade.client.util.autotrade.dto.user.ref;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class FcdReferralBalanceDto {
    private Long tdId;
    private BigDecimal balance;
    private BigDecimal referralBalance;
    private BigDecimal totalReferralEarnings;
    private BigDecimal totalDeposits;
}
