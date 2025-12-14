package cs.youtrade.autotrade.client.telegram.menu.start.user.params;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserParamsState extends AbstractTextMenuState<UserParamsMenu> {
    private final ParamsEndpoint paramsEndpoint;

    public UserParamsState(
            UserTextMessageSender sender,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS;
    }

    @Override
    public UserParamsMenu getOption(String optionStr) {
        return UserParamsMenu.valueOf(optionStr);
    }

    @Override
    public UserParamsMenu[] getOptions() {
        return UserParamsMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserParamsMenu t) {
        return switch (t) {
            case PARAMS_RENAME -> UserMenu.PARAMS_RENAME_STAGE_1;
            case PARAMS_TO_AUTOBUY -> UserMenu.AUTOBUY;
            case PARAMS_TO_AUTOSELL -> UserMenu.AUTOSELL;
            case PARAMS_TO_FOLLOW -> UserMenu.FOLLOW;
            case PARAMS_TO_TOKENS -> UserMenu.TOKEN;
            case RETURN -> UserMenu.USER;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var restAns = paramsEndpoint.getCurrent(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return getParamsInfo(fcd.getData());
    }

    private String getParamsInfo(FcdParamsGetDto fcd) {
        return String.format("""
                        Параметры вашего аккаунта:
                        Имя: %s
                        🆔 params-ID=%s
                        """,
                fcd.getGivenName(),
                fcd.getTdpId()
        );
    }
}
