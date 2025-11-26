package cs.youtrade.autotrade.client.telegram.prototype.menu.text;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.AbstractMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public abstract class AbstractTextMenuState<MENU_TYPE extends IMenuEnum>
        extends AbstractMenuState<MENU_TYPE, SendMessage> {
    public AbstractTextMenuState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    public SendMessage buildMessage(UserData userData) {
        var builder = SendMessage.builder();
        builder.chatId(userData.getChatId());

        String header = getHeaderText(userData);
        if (header == null)
            return null;

        builder.text(header);
        builder.replyMarkup(buildMarkup());

        return builder.build();
    }
}
