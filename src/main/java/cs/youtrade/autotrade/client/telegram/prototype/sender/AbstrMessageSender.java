package cs.youtrade.autotrade.client.telegram.prototype.sender;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.data.AbstractUserData;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@RequiredArgsConstructor
public abstract class AbstrMessageSender<USER extends AbstractUserData, MESSAGE>
        implements MessageSenderInt<USER, MESSAGE> {
    protected static final String SERVER_ERROR_MES =
            "🚫 Сервер временно недоступен. Попробуйте через несколько минут или перейдите в главное меню (/start).";
    protected final TelegramSendMessageService sender;

    @Override
    public void sendDefErrMes(TelegramClient bot, long chatId) {
        sender.sendMessage(bot, chatId, SERVER_ERROR_MES);
    }

    @Override
    public void sendTextMes(TelegramClient bot, long chatId, String text) {
        sender.sendMessage(bot, chatId, text);
    }

    @Override
    public void replyCallback(TelegramClient bot, USER user, Update update) {
        if (!update.hasCallbackQuery())
            return;

        String callbackId = update.getCallbackQuery().getId();
        AnswerCallbackQuery ack = AnswerCallbackQuery
                .builder()
                .callbackQueryId(callbackId)
                .build();
        sender.sendMessage(bot, user.getChatId(), ack);
    }

    @Override
    public void deleteMes(TelegramClient bot, USER user, Update update) {
        sender.deleteMes(bot, user.getChatId(), update);
    }
}
