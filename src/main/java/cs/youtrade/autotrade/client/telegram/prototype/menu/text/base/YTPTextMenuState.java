package cs.youtrade.autotrade.client.telegram.prototype.menu.text.base;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.IMenuEnum;
import cs.youtrade.telegram.buttons.menu.text.AbstractTextMenuState;
import cs.youtrade.telegram.buttons.sender.text.BaseTextMessageSender;

public abstract class YTPTextMenuState<MENU extends IMenuEnum> extends AbstractTextMenuState<UserData, MENU, UserMenu> {
    public YTPTextMenuState(
            BaseTextMessageSender<UserData> sender
    ) {
        super(sender);
    }
}
