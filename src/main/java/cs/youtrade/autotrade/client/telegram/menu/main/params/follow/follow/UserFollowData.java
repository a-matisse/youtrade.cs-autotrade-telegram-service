package cs.youtrade.autotrade.client.telegram.menu.main.params.follow.follow;

import cs.youtrade.autotrade.client.telegram.menu.main.params.follow.UserFollowOperationType;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import lombok.Data;

@Data
public class UserFollowData {
    private UserFollowOperationType type;
    private Long yourTdpId;
    private ParamsCopyOptions pco;
}
