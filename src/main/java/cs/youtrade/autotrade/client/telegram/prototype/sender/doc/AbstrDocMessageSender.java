package cs.youtrade.autotrade.client.telegram.prototype.sender.doc;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.data.AbstractUserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.AbstrMessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstrDocMessageSender<USER extends AbstractUserData> extends AbstrMessageSender<USER, SendDocument> {
    public AbstrDocMessageSender(
            TelegramSendMessageService sender
    ) {
        super(sender);
    }


    @Override
    public void sendMessage(TelegramClient bot, USER user, SendDocument mes) {
        long chatId = user.getChatId();
        if (mes == null)
            sendDefErrMes(bot, chatId);
        else
            sender.sendMessage(bot, chatId, mes);
    }
}
