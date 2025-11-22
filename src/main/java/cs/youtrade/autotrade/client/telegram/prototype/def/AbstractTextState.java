package cs.youtrade.autotrade.client.telegram.prototype.def;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.MessageSenderInt;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public abstract class AbstractTextState extends AbstractDefState<UserData, SendMessage> {
    public AbstractTextState(
            MessageSenderInt<UserData, SendMessage> sender
    ) {
        super(sender);
    }

    @Override
    public SendMessage buildMessage(UserData e) {
        var builder = SendMessage.builder();
        builder.chatId(e.getChatId());
        builder.text(getMessage());

        return builder.build();
    }

    protected abstract String getMessage();
}
