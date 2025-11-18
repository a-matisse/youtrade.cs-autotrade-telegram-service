package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdParamsCopyReqDto extends AbstractFcdDto {
    private Long thatChatId;
    private Long yourChatId;
    private Long thatUId;
    private Long yourUId;
    private String yourTdpName;
    private FcdParamsCopyReqCallbackDto callbackDto;
}
