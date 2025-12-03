package cs.youtrade.autotrade.client.telegram.prototype.def;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.MessageSenderInt;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstractTextState extends AbstractDefState<UserData, SendMessage> {
    public AbstractTextState(
            MessageSenderInt<UserData, SendMessage> sender
    ) {
        super(sender);
    }

    @Override
    public SendMessage buildMessage(TelegramClient bot, UserData e) {
        return SendMessage
                .builder()
                .chatId(e.getChatId())
                .text(getMessage(e))
                .parseMode(ParseMode.HTML)
                .build();
    }

    protected abstract String getMessage(UserData user);
}
