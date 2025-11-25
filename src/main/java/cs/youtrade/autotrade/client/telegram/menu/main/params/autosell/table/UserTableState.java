package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserTableState extends AbstractTextMenuState<UserTableMenu> {
    public UserTableState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TABLE;
    }

    @Override
    public UserTableMenu getOption(String optionStr) {
        return UserTableMenu.valueOf(optionStr);
    }

    @Override
    public UserTableMenu[] getOptions() {
        return UserTableMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserTableMenu t) {
        return null;
    }

    @Override
    public String getHeaderText(UserData userData) {
        return "";
    }
}
