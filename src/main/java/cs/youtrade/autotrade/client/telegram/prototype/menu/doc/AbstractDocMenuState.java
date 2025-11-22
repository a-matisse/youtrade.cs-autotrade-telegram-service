package cs.youtrade.autotrade.client.telegram.prototype.menu.doc;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.AbstractMenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstractDocMenuState <MENU_TYPE extends MenuEnumInterface>
        extends AbstractMenuState<MENU_TYPE, SendDocument> {
    public AbstractDocMenuState(
            TelegramSendMessageService sender
    ) {
        super(sender);
    }

    @Override
    public SendDocument buildMessage(UserData userData) {
        var builder = SendDocument.builder();
        builder.chatId(userData.getChatId());

        String header = getHeaderText(userData);
        if (header == null)
            return null;

        InputFile doc = getHeaderDoc(userData);
        if (doc == null)
            return null;

        builder.caption(header);
        builder.document(doc);
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

    public abstract InputFile getHeaderDoc(UserData userData);
}
