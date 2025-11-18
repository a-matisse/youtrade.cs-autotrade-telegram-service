package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdParamsCopyResDto extends AbstractFcdDto {
    private Long thatChatId;
    private Long yourChatId;
    private Long thatTdpId;
    private Long yourTdpId;
    private ParamsCopyOptions pco;
    private Boolean found;
}
