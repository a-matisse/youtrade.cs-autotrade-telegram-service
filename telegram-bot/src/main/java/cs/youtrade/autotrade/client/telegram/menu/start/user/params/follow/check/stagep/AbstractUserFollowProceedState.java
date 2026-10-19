package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.check.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.prototype.UserInitializer;
import cs.youtrade.autotrade.client.telegram.prototype.UserRegistry;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.check.UserFollowCheckRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyReqCallbackDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyResDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstractUserFollowProceedState extends YTPTerminalTextMenuState {
    private final UserFollowCheckRegistry registry;
    private final ParamsEndpoint endpoint;
    private final UserRegistry users;
    private final UserInitializer initializer;

    public AbstractUserFollowProceedState(
            UserTextMessageSender sender,
            UserFollowCheckRegistry registry,
            ParamsEndpoint endpoint,
            BotTexts botTexts,
            UserRegistry users,
            UserInitializer initializer
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.endpoint = endpoint;
        this.users = users;
        this.initializer = initializer;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.follow.UserFollowCheck");
        var data = registry.remove(userData);
        if (data == null)
            return translations.get("there-are-no-requests-to-process-right");

        var callbackDto = data.getDto().getCallbackDto();
        var restAns = switch (data.getType()) {
            case FOLLOW -> endpoint.proceedFollow(getCallback(callbackDto));
            case COPY -> endpoint.proceedCopy(getCallback(callbackDto));
        };
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        String mes = getMessage(fcd, translations);
        sendThat(bot, fcd);
        return mes;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.FOLLOW_CHECK;
    }

    private void sendThat(TelegramClient bot, FcdParamsCopyResDto fcd) {
        var recipient = users.getOrCreateUser(fcd.getThatChatId(), initializer::initUser);
        sender.sendTextMes(bot, recipient, getMessage(fcd,
                botTexts.forUser(recipient, "parameters.follow.UserFollowCheck")));
    }

    public abstract String getCallback(FcdParamsCopyReqCallbackDto dto);

    public abstract String getMessage(FcdParamsCopyResDto fcd, LocalizedTexts translations);
}
