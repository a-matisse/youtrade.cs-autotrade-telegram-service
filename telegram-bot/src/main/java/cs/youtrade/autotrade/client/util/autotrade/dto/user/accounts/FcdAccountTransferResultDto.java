package cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdAccountTransferResultDto extends AbstractFcdDto {
    private Long tokenId;
    private FcdAccountTransferStatus status;
    private boolean buyTokenDeleted;
    private boolean sellTokenDeleted;
    private boolean transferred;
}
