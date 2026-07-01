package cs.youtrade.autotrade.client.telegram.prototype.menu.doc.base;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.ErrorMessageGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.IMenuEnum;
import cs.youtrade.telegram.buttons.menu.doc.AbstractDocMenuState;
import cs.youtrade.telegram.buttons.sender.doc.BaseDocMessageSender;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class YTPDocMenuState<C, MENU extends IMenuEnum> extends AbstractDocMenuState<C, UserData, UserMenu, MENU> {
    public YTPDocMenuState(
            BaseDocMessageSender<UserData> sender
    ) {
        super(sender);
    }

    @Override
    public void sendDefErrMes(TelegramClient bot, UserData userData) {
        sender.sendTextMes(bot, userData, ErrorMessageGenerator.getErrorText());
    }
}
