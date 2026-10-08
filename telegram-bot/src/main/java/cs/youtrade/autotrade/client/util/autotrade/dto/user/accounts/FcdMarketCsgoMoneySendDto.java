package cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdMarketCsgoMoneySendDto extends AbstractFcdDto {
    private Long sourceTokenId;
    private Long failedAccountId;
    private BigDecimal amount;
    private String currency;
    private boolean outcomeUnknown;
}
