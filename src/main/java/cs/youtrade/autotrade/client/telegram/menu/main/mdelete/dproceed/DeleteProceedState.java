package cs.youtrade.autotrade.client.telegram.menu.main.mdelete.dproceed;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.mdelete.ParamsDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.main.mdelete.ParamsDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenu;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsDeleteResDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class DeleteProceedState extends AbstractTextMenuState<TerminalMenu> {
    private final ParamsDeleteRegistry registry;
    private final ParamsEndpoint paramsEndpoint;

    public DeleteProceedState(
            UserTextMessageSender sender,
            ParamsDeleteRegistry registry,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.registry = registry;
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_PARAMETERS_DELETE_PROCEED;
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
        ParamsDeleteData deleteData = registry.completeAndRemove(userData);
        RestAnswer<FcdParamsDeleteResDto> restAns = paramsEndpoint.proceedDelete(userData.getChatId(), deleteData.getCallback());
        if (restAns.getStatus() >= 300)
            return SERVER_ERROR_MES;

        var fcd = restAns.getResponse();
        if (fcd.getCause() != null)
            return fcd.getCause();

        return String.format("Параметры [%s] удалены", fcd.getTdpId());
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, TerminalMenu t) {
        return switch (t) {
            case RETURN -> UserMenu.MAIN;
        };
    }
}
