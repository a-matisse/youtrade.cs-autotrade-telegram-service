package cs.youtrade.autotrade.client.telegram.prototype.def;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.MessageSenderInt;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public abstract class AbstractDocState<C> extends AbstractDefState<UserData, SendDocument> {
    public AbstractDocState(
            MessageSenderInt<UserData, SendDocument> sender
    ) {
        super(sender);
    }

    @Override
    public SendDocument buildMessage(UserData user) {
        C content = getContent(user);
        if (content == null)
            return null;

        InputFile doc = getHeaderDoc(user, content);
        if (doc == null)
            return null;

        String header = getHeader(user, content);
        if (header == null)
            return null;

        return SendDocument
                .builder()
                .chatId(user.getChatId())
                .caption(header)
                .document(doc)
                .parseMode(ParseMode.HTML)
                .build();
    }

    private String getHeader(UserData user, C content) {
        String mainHeader = getHeaderText(user);
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
    }

    public abstract String getHeaderText(UserData user);

    public abstract C getContent(UserData user);

    public abstract InputFile getHeaderDoc(UserData user, C content);

    public String getHeaderDocText(UserData user, C content) {
        return null;
    }
}
