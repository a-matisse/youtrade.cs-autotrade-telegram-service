package cs.youtrade.autotrade.client.telegram.menu.start.user.follow.check;

import cs.youtrade.autotrade.client.telegram.menu.start.user.follow.UserFollowOperationType;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyReqDto;
import lombok.Data;

@Data
public class UserFollowCheckData {
    private final UserFollowOperationType type;
    private final FcdParamsCopyReqDto dto;
    private final ParamsCopyOptions pco;
}
