package cs.youtrade.autotrade.client.telegram.prototype.menu.text.base;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.ErrorMessageGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.IMenuEnum;
import cs.youtrade.telegram.buttons.menu.text.AbstractTextMenuState;
import cs.youtrade.telegram.buttons.sender.text.BaseTextMessageSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class YTPTextMenuState<MENU extends IMenuEnum> extends AbstractTextMenuState<UserData, MENU, UserMenu> {
    public YTPTextMenuState(
            BaseTextMessageSender<UserData> sender
    ) {
        super(sender);
    }

    @Override
    public void sendDefErrMes(TelegramClient bot, UserData userData) {
        sender.sendTextMes(bot, userData, ErrorMessageGenerator.getErrorText());
    }
}
