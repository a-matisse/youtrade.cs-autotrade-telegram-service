package cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import cs.youtrade.autotrade.client.util.autotrade.util.accounts.FcdAccountV2Dto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdAccountsPageDto extends AbstractFcdDto {
    private Page<FcdAccountV2Dto> accounts;
}
