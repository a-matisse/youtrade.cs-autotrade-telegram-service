package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserAutoSellState extends AbstractTextMenuState<UserAutoSellMenu> {
    private final ParamsEndpoint paramsEndpoint;

    public UserAutoSellState(
            UserTextMessageSender sender,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL;
    }

    @Override
    public UserAutoSellMenu getOption(String optionStr) {
        return UserAutoSellMenu.valueOf(optionStr);
    }

    @Override
    public UserAutoSellMenu[] getOptions() {
        return UserAutoSellMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserAutoSellMenu t) {
        return switch (t) {
            case AUTOSELL_UPDATE_FIELD -> null;
            case AUTOSELL_SWITCH_EVAL_MODE -> UserMenu.AUTOSELL_SWITCH_EVAL_MODE;
            case AUTOSELL_SWITCH_EVAL_S1 -> UserMenu.AUTOSELL_SWITCH_EVAL_MODE_S1;
            case AUTOBUY_TOGGLE_AUTOBUY -> null;
            case AUTOSELL_TO_TABLES -> null;
            case RETURN -> UserMenu.PARAMS;
        };
    }

    @Override
    public String getHeaderText(UserData userData) {
        return "";
    }
}
