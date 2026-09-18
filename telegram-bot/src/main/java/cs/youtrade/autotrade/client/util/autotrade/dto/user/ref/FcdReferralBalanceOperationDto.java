package cs.youtrade.autotrade.client.util.autotrade.dto.user.ref;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class FcdReferralBalanceOperationDto {
    private String requestId;
    private String type;
    private String status;
    private BigDecimal amount;
    private BigDecimal networkFee;
    private BigDecimal payoutAmount;
    private BigDecimal balance;
    private BigDecimal referralBalance;
    private String createdAt;
    private String updatedAt;
    private String completedAt;
}
