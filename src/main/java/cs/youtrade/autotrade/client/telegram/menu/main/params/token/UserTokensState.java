package cs.youtrade.autotrade.client.telegram.menu.main.params.token;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserTokensState extends AbstractTextMenuState<UserTokensMenu> {
    public UserTokensState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN;
    }

    @Override
    public UserTokensMenu getOption(String optionStr) {
        return UserTokensMenu.valueOf(optionStr);
    }

    @Override
    public UserTokensMenu[] getOptions() {
        return UserTokensMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserTokensMenu t) {
        return switch (t) {
            case TOKEN_GET -> UserMenu.TOKEN_GET;
            case TOKEN_ADD -> UserMenu.TOKEN_ADD_STAGE_CHOOSE;
            case TOKEN_REMOVE -> UserMenu.TOKEN_REMOVE_STAGE_1;
            case TOKEN_RENAME -> UserMenu.TOKEN_RENAME_STAGE_1;
            case RETURN -> UserMenu.PARAMS;
        };
    }

    @Override
    public String getHeaderText(UserData userData) {
        return "";
    }
}
