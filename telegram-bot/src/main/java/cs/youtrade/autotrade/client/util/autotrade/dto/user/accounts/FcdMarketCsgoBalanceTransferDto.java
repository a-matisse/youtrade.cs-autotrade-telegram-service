package cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdMarketCsgoBalanceTransferDto extends AbstractFcdDto {
    private Long destinationTokenId;
    private Long failedAccountId;
    private int transferredTokens;
    private String currency;
    private BigDecimal destinationBalanceBefore;
    private BigDecimal destinationBalanceAfter;
    private String balanceSource;
    private Map<String, BigDecimal> transferredAmounts;
    private List<AccountTransfer> accounts;

    @Data
    public static class AccountTransfer {
        private Long tokenId;
        private String currency;
        private BigDecimal balanceBefore;
        private BigDecimal requestedAmount;
        private BigDecimal transferredAmount;
        private BigDecimal balanceAfter;
        private String status;
        private String cause;
    }
}
