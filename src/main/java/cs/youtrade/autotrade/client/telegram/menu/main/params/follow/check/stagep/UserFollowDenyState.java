package cs.youtrade.autotrade.client.telegram.menu.main.params.follow.check.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.follow.check.UserFollowCheckRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyReqCallbackDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyResDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;

@Service
public class UserFollowDenyState extends AbstractUserFollowProceedState {
    public UserFollowDenyState(
            UserTextMessageSender sender,
            UserFollowCheckRegistry registry,
            ParamsEndpoint endpoint
    ) {
        super(sender, registry, endpoint);
    }

    @Override
    public String getCallback(FcdParamsCopyReqCallbackDto dto) {
        return dto.getDecline();
    }

    @Override
    public String getMessage(FcdParamsCopyResDto fcd) {
        return String.format("❌ Не удалось активировать следование по причине: %s",
                fcd.getCause());
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_CHECK_DENY;
    }
}
