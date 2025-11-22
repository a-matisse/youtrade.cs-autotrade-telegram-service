package cs.youtrade.autotrade.client.telegram.menu.main;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralAccInfoDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class UserMainState extends AbstractTextMenuState<UserMainMenu> {
    private final GeneralEndpoint generalEndpoint;

    public UserMainState(
            UserTextMessageSender sender,
            GeneralEndpoint generalEndpoint
    ) {
        super(sender);
        this.generalEndpoint = generalEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN;
    }

    @Override
    public UserMainMenu getOption(String optionStr) {
        return UserMainMenu.valueOf(optionStr);
    }

    @Override
    public UserMainMenu[] getOptions() {
        return UserMainMenu.values();
    }

    @Override
    public String getHeaderText(UserData userData) {
        return getHeader(userData);
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserMainMenu t) {
        return switch (t) {
            case MAIN_TO_PARAMETERS -> UserMenu.PARAMS;
            case MAIN_PARAMETERS_LIST -> null;
            case MAIN_PARAMETERS_SWITCH -> null;
            case MAIN_PARAMETERS_CREATE -> null;
            case MAIN_PARAMETERS_DELETE -> null;
            case MAIN_GET_NEWEST_ITEMS -> null;
        };
    }

    private String getHeader(UserData userData) {
        RestAnswer<FcdGeneralAccInfoDto> restAns = generalEndpoint.viewAccInfo(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return SERVER_ERROR_MES;

        var ans = restAns.getResponse();
        if (ans.getCause() != null)
            return ans.getCause();

        return getHeader(ans);
    }

    private String getHeader(FcdGeneralAccInfoDto fcd) {
        return String.format("""
                        🆔 Ваш id: %d
                        💰 Остаток баланса: $%.2f
                        """,
                fcd.getTdId(),
                fcd.getBalance());
    }
}
