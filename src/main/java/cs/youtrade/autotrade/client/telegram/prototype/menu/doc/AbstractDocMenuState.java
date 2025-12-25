package cs.youtrade.autotrade.client.telegram.prototype.menu.doc;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import cs.youtrade.autotrade.client.telegram.prototype.TelegramFileDownloader;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.AbstractMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;

public abstract class AbstractDocMenuState<C, MENU_TYPE extends IMenuEnum>
        extends AbstractMenuState<MENU_TYPE, SendDocument> {
    public AbstractDocMenuState(
            UserDocMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public SendDocument buildMessage(TelegramClient bot, UserData user) {
        C content = getContent(user);
        if (content == null)
            return null;

        InputFile doc = getHeaderDoc(user, content);
        if (doc == null)
            return null;

        String header = getHeader(bot, user, content);
        if (header == null)
            return null;

        return SendDocument
                .builder()
                .chatId(user.getChatId())
                .caption(header)
                .document(doc)
                .replyMarkup(buildMarkup(user))
                .parseMode(ParseMode.HTML)
                .build();
    }

    private String getHeader(TelegramClient bot, UserData user, C content) {
        try {
            String mainHeader = getHeaderText(bot, user);
            if (mainHeader == null)
                return null;

            String docHeader = getHeaderDocText(user, content);
            if (docHeader == null)
                return mainHeader;

            return String.format("""
                        %s
                        
                        %s
                        """,
                    mainHeader,
                    docHeader
            );
        } catch (Exception e) {
            return null;
        }
    }

    public File downloadFile(TelegramClient bot, Document doc) throws TelegramApiException, IOException {
        return TelegramFileDownloader.downloadFile(bot, doc);
    }

    public abstract C getContent(UserData user);

    public abstract InputFile getHeaderDoc(UserData user, C content);

    public String getHeaderDocText(UserData user, C content) {
        return null;
    }
}
