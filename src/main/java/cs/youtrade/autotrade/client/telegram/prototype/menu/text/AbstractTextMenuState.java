package cs.youtrade.autotrade.client.telegram.prototype.menu.text;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.AbstractMenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstractTextMenuState<MENU_TYPE extends MenuEnumInterface>
        extends AbstractMenuState<MENU_TYPE, SendMessage> {
    public AbstractTextMenuState(
            TelegramSendMessageService sender
    ) {
        super(sender);
    }

    public SendMessage buildMessage(UserData userData) {
        var builder = SendMessage.builder();
        builder.chatId(userData.getChatId());

        String header = getHeaderText(userData);
        if (header == null)
            return null;

        builder.text(header);
        builder.replyMarkup(buildMarkup());

        return builder.build();
    }

    @Override
    public void sendMessage(TelegramClient bot, Update update, UserData e) {
        var message = buildMessage(e);
        if (message == null)
            sendDefErrMes(bot, e.getChatId());

        sender.sendMessage(bot, e.getChatId(), buildMessage(e));
    }
}
