package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.check.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.UserRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.UserInitializer;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.check.UserFollowCheckRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyReqCallbackDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyResDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;

@Service
public class UserFollowDenyState extends AbstractUserFollowProceedState {
    public UserFollowDenyState(
            UserTextMessageSender sender,
            UserFollowCheckRegistry registry,
            ParamsEndpoint endpoint,
            BotTexts botTexts,
            UserRegistry users,
            UserInitializer initializer
    ) {
        super(sender, registry, endpoint, botTexts, users, initializer);
    }

    @Override
    public String getCallback(FcdParamsCopyReqCallbackDto dto) {
        return dto.getDecline();
    }

    @Override
    public String getMessage(FcdParamsCopyResDto fcd, LocalizedTexts translations) {
        return String.format("%s %s: %s",
                DynamicEmoji.ERROR.getEmoji(),
                translations.get("could-not-enable-following-reason"),
                fcd.getCause());
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_CHECK_DENY;
    }
}
