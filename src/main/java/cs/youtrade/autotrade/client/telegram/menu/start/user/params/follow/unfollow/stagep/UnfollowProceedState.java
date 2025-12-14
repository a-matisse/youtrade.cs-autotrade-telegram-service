package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.unfollow.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.unfollow.UserUnfollowRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UnfollowProceedState extends AbstractTerminalTextMenuState {
    private final UserUnfollowRegistry registry;
    private final ParamsEndpoint endpoint;

    public UnfollowProceedState(
            UserTextMessageSender sender,
            UserUnfollowRegistry registry,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_UNFOLLOW_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        long chatId = user.getChatId();
        var data = registry.remove(user);
        var restAns = endpoint.unfollow(chatId, data.getFollowId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("✅ Прекращено следование за params-ID=%d", fcd.getData());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.FOLLOW;
    }
}
