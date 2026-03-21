package cs.youtrade.autotrade.client.telegram.prototype.menu.doc.base;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.IMenuEnum;
import cs.youtrade.telegram.buttons.menu.doc.AbstractDocMenuState;
import cs.youtrade.telegram.buttons.sender.doc.BaseDocMessageSender;

public abstract class YTPDocMenuState<C, MENU extends IMenuEnum> extends AbstractDocMenuState<C, UserData, UserMenu, MENU> {
    public YTPDocMenuState(
            BaseDocMessageSender<UserData> sender
    ) {
        super(sender);
    }
}
