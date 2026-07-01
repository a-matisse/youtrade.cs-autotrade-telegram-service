package cs.youtrade.autotrade.client.util.autotrade.dto.norole;

import cs.youtrade.autotrade.client.util.autotrade.TopUpType;
import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdTopUpDto extends AbstractFcdDto {
    private Long userTdId;
    private String idempotencyKey;
    private TopUpType type;
    private String url;
    private BigDecimal usdAmount;
    private BigDecimal rubAmount;
    private List<Long> adminChats;
}
