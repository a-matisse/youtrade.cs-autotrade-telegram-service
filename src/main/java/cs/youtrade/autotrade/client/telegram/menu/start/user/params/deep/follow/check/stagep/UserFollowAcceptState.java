package cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.follow.check.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.follow.check.UserFollowCheckRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyReqCallbackDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyResDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;

@Service
public class UserFollowAcceptState extends AbstractUserFollowProceedState {
    public UserFollowAcceptState(
            UserTextMessageSender sender,
            UserFollowCheckRegistry registry,
            ParamsEndpoint endpoint
    ) {
        super(sender, registry, endpoint);
    }

    @Override
    public String getCallback(FcdParamsCopyReqCallbackDto dto) {
        return dto.getConfirm();
    }

    @Override
    public String getMessage(FcdParamsCopyResDto fcd) {
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("✅ Активировано следование: params-ID=%d → params-ID=%d",
                fcd.getThatTdpId(), fcd.getYourTdpId());
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_CHECK_ACCEPT;
    }
}
