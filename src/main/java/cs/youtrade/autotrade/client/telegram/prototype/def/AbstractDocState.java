package cs.youtrade.autotrade.client.telegram.prototype.def;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.MessageSenderInt;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public abstract class AbstractDocState extends AbstractDefState<UserData, SendDocument> {
    public AbstractDocState(
            MessageSenderInt<UserData, SendDocument> sender
    ) {
        super(sender);
    }

    @Override
    public SendDocument buildMessage(UserData e) {
        var builder = SendDocument.builder();
        builder.chatId(e.getChatId());
        builder.document(getFile());
        builder.caption(getMessage());

        return builder.build();
    }

    protected abstract String getMessage();

    protected abstract InputFile getFile();
}
