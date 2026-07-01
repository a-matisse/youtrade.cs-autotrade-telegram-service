package cs.youtrade.autotrade.client.telegram.prototype.menu.text;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenu;
import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenuInt;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class YTPTerminalTextMenuState extends YTPTextMenuState<TerminalMenu> implements TerminalMenuInt {
    public YTPTerminalTextMenuState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public TerminalMenu getOption(String optionStr) {
        return TerminalMenu.valueOf(optionStr);
    }

    @Override
    public TerminalMenu[] getOptions(UserData userData) {
        return TerminalMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, TerminalMenu t) {
        return switch (t) {
            case RETURN -> retState();
        };
    }
}
