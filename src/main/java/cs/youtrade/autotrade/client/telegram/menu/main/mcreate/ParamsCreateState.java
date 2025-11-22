package cs.youtrade.autotrade.client.telegram.menu.main.mcreate;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenu;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class ParamsCreateState extends AbstractTextMenuState<TerminalMenu> {
    private final ParamsEndpoint paramsEndpoint;

    public ParamsCreateState(
            TelegramSendMessageService sender,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_PARAMETERS_CREATE;
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
    public String getHeaderText(UserData userData) {
        RestAnswer<FcdDefaultDto<Long>> restAns = paramsEndpoint.create(userData.getChatId(), MarketType.LIS_SKINS, "");
        if (restAns.getStatus() >= 300)
            return SERVER_ERROR_MES;

        var ans = restAns.getResponse();
        if (ans.getCause() != null)
            return ans.getCause();

        return String.format("Новые параметры созданы (ID=%d)", ans.getData());
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, TerminalMenu t) {
        return switch (t) {
            case RETURN -> UserMenu.MAIN;
        };
    }
}
