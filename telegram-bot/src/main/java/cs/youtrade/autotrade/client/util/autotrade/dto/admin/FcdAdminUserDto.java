package cs.youtrade.autotrade.client.util.autotrade.dto.admin;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdAdminUserDto extends AbstractFcdDto {
    private Long chatId;
    private Long tdId;
    private BigDecimal subBalanceLeft;
}
