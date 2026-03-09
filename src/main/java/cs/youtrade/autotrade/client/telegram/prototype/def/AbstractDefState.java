package cs.youtrade.autotrade.client.telegram.prototype.def;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.dto.UserStateData;
import cs.youtrade.autotrade.client.telegram.prototype.data.AbstractUserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.MessageSenderInt;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@RequiredArgsConstructor
@Log4j2
public abstract class AbstractDefState<USER extends AbstractUserData, MESSAGE>
        implements DefStateInt<USER, UserMenu, MESSAGE> {
    protected static final String SERVER_ERROR_MES = "🚫 Сервис временно недоступен. Попробуйте через несколько минут...";
    protected final MessageSenderInt<USER, MESSAGE> sender;

    @Override
    public void executeOnState(TelegramClient bot, USER user, Update update) {
        sender.sendMessage(bot, user, buildMessage(bot, user));
    }

    @Override
    public void executeOnState(TelegramClient bot, USER user, UserStateData lastState, Object data) {
        sender.sendMessage(bot, user, buildMessage(bot, user, data));
    }

    @Override
    public MESSAGE buildMessage(TelegramClient bot, USER user, Object data) {
        return buildMessage(bot, user);
    }

    public void executeSide(TelegramClient bot, Update update, UserData userData) {
    }

    public void sendDefErrMes(TelegramClient bot, long chatId) {
        sender.sendDefErrMes(bot, chatId);
    }

    public String getDefaultSpacer() {
        return "━━━━━━━━━━━━━━━━━━━━━";
    }
}
