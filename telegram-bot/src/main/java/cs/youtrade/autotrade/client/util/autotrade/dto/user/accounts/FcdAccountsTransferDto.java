package cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdAccountsTransferDto extends AbstractFcdDto {
    private Long sourceParametersId;
    private Long destinationParametersId;
    private String destinationParametersName;
    private List<FcdAccountTransferResultDto> answers;
}
