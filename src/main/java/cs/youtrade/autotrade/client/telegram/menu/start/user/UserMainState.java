package cs.youtrade.autotrade.client.telegram.menu.start.user;

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
        return UserMenu.USER;
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
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return getHeader(userData);
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserMainMenu t) {
        return switch (t) {
            case MAIN_TO_PARAMETERS -> UserMenu.PARAMS;
            case MAIN_PARAMETERS_LIST -> UserMenu.MAIN_PARAMETERS_LIST;
            case MAIN_PARAMETERS_SWITCH -> UserMenu.MAIN_PARAMETERS_SWITCH_STAGE_1;
            case MAIN_PARAMETERS_CREATE -> UserMenu.MAIN_PARAMETERS_CREATE_STAGE_1;
            case MAIN_PARAMETERS_DELETE -> UserMenu.MAIN_PARAMETERS_DELETE_STAGE_1;
            case MAIN_GET_NEWEST_ITEMS -> UserMenu.MAIN_GET_NEWEST_ITEMS_STAGE_1;
            case RETURN -> UserMenu.START;
        };
    }

    private String getHeader(UserData userData) {
        RestAnswer<FcdGeneralAccInfoDto> restAns = generalEndpoint.viewAccInfo(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return SERVER_ERROR_MES;

        var ans = restAns.getResponse();
        if (!ans.isResult())
            return ans.getCause();

        return getHeader(ans);
    }

    private String getHeader(FcdGeneralAccInfoDto fcd) {
        return String.format("""
                        🆔 Ваш id: %d
                        💰 Остаток баланса: $%.2f
                        
                        Текущие параметры:
                        Имя: %s
                        🆔 params-ID=%s
                        """,
                fcd.getTdId(),
                fcd.getBalance(),
                fcd.getGivenName(),
                fcd.getTdpId()
        );
    }
}
