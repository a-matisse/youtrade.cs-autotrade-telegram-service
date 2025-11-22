package cs.youtrade.autotrade.client.telegram.prototype.sender.text;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.data.AbstractUserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.AbstrMessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstrTextMessageSender<USER extends AbstractUserData> extends AbstrMessageSender<USER, SendMessage> {
    public AbstrTextMessageSender(TelegramSendMessageService sender) {
        super(sender);
    }

    @Override
    public void sendMessage(TelegramClient bot, USER user, SendMessage mes) {
        long chatId = user.getChatId();
        if (mes == null)
            sendDefErrMes(bot, chatId);
        else
            sender.sendMessage(bot, chatId, mes);
    }
}
