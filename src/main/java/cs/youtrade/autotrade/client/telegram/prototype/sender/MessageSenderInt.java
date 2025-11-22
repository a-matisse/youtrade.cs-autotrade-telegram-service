package cs.youtrade.autotrade.client.telegram.prototype.sender;

import cs.youtrade.autotrade.client.telegram.prototype.data.AbstractUserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface MessageSenderInt<USER extends AbstractUserData, MESSAGE> {
    /**
     * Метод отправки сообщения пользователю
     */
    void sendMessage(TelegramClient bot, USER user, MESSAGE mes);

    void sendDefErrMes(TelegramClient bot, long chatId);

    void sendTextMes(TelegramClient bot, long chatId, String text);

    void replyCallback(TelegramClient bot, Update update, UserData userData);
}
