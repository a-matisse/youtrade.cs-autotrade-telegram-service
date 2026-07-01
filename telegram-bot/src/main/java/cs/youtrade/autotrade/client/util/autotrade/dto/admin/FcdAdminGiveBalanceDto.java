package cs.youtrade.autotrade.client.util.autotrade.dto.admin;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdAdminGiveBalanceDto extends AbstractFcdDto {
    private Long tdId;
    private Long chatId;
    private BigDecimal balance;
    private Collection<FcdAdminRoleDataDto> roles;
}
