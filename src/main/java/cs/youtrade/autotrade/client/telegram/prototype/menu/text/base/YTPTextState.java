package cs.youtrade.autotrade.client.telegram.prototype.menu.text.base;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.def.AbstractTextState;
import cs.youtrade.telegram.buttons.sender.IMessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public abstract class YTPTextState extends AbstractTextState<UserData, UserMenu> {
    public YTPTextState(IMessageSender<UserData, SendMessage, EditMessageText> sender) {
        super(sender);
    }
}
