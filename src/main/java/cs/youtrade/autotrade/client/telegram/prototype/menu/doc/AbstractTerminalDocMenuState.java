package cs.youtrade.autotrade.client.telegram.prototype.menu.doc;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenu;
import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenuInt;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstractTerminalDocMenuState<C> extends AbstractDocMenuState<C, TerminalMenu> implements TerminalMenuInt {
    public AbstractTerminalDocMenuState(
            UserDocMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public TerminalMenu getOption(String optionStr) {
        return TerminalMenu.valueOf(optionStr);
    }

    @Override
    public TerminalMenu[] getOptions() {
        return TerminalMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, TerminalMenu t) {
        return switch (t) {
            case RETURN -> retState();
        };
    }
}
