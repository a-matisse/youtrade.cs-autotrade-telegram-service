package cs.youtrade.autotrade.client.telegram.prototype.menu.doc;

import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.AbstractMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public abstract class AbstractDocMenuState <MENU_TYPE extends MenuEnumInterface>
        extends AbstractMenuState<MENU_TYPE, SendDocument> {
    public AbstractDocMenuState(
            UserDocMessageSender sender
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

    public abstract InputFile getHeaderDoc(UserData user);
}
