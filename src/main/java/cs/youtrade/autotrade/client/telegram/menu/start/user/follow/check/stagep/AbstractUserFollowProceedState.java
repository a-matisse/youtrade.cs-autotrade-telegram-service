package cs.youtrade.autotrade.client.telegram.menu.start.user.follow.check.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.follow.check.UserFollowCheckRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyReqCallbackDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyResDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstractUserFollowProceedState extends AbstractTerminalTextMenuState {
    private final UserFollowCheckRegistry registry;
    private final ParamsEndpoint endpoint;

    public AbstractUserFollowProceedState(
            UserTextMessageSender sender,
            UserFollowCheckRegistry registry,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var data = registry.remove(userData);
        if (data == null)
            return "На данный момент отсутствуют заявки для обработки...";

        var callbackDto = data.getDto().getCallbackDto();
        var restAns = switch (data.getType()) {
            case FOLLOW -> endpoint.proceedFollow(getCallback(callbackDto));
            case COPY -> endpoint.proceedCopy(getCallback(callbackDto));
        };
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        String mes = getMessage(fcd);
        sendThat(bot, fcd, mes);
        return mes;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.FOLLOW_CHECK;
    }

    private void sendThat(TelegramClient bot, FcdParamsCopyResDto fcd, String mes) {
        sender.sendTextMes(bot, fcd.getThatChatId(), mes);
    }

    public abstract String getCallback(FcdParamsCopyReqCallbackDto dto);

    public abstract String getMessage(FcdParamsCopyResDto fcd);
}
