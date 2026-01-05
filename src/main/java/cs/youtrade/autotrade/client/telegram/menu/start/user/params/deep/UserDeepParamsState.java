package cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep;

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
public class UserDeepParamsState extends AbstractTextMenuState<UserDeepParamsMenu> {
    private final ParamsEndpoint paramsEndpoint;

    public UserDeepParamsState(
            UserTextMessageSender sender,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.DEEP_PARAMS;
    }

    @Override
    public UserDeepParamsMenu getOption(String optionStr) {
        return UserDeepParamsMenu.valueOf(optionStr);
    }

    @Override
    public UserDeepParamsMenu[] getOptions() {
        return UserDeepParamsMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserDeepParamsMenu t) {
        return switch (t) {
            case DEEP_PARAMS_TO_AUTOBUY -> UserMenu.AUTOBUY;
            case DEEP_PARAMS_TO_AUTOSELL -> UserMenu.AUTOSELL;
            case DEEP_PARAMS_RENAME -> UserMenu.DEEP_PARAMS_RENAME_STAGE_1;
            case RETURN -> UserMenu.PARAMS;
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
        return """
                🧠 <b>Углублённые параметры</b>
                ━━━━━━━━━━━━━━━━━━━━━
                
                Тонкая настройка логики покупки, продажи и фильтрации.
                Изменения применяются к новым операциям.
                
                Выберите меню настройки:
                """;
    }
}
