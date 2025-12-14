package cs.youtrade.autotrade.client.telegram.menu.start.user.delete.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.delete.ParamsDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.delete.ParamsDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsDeleteResDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class DeleteProceedState extends AbstractTerminalTextMenuState {
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
        return UserMenu.MAIN_PARAMETERS_DELETE_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        ParamsDeleteData deleteData = registry.remove(userData);
        RestAnswer<FcdParamsDeleteResDto> restAns = paramsEndpoint.proceedDelete(userData.getChatId(), deleteData.getCallback());
        if (restAns.getStatus() >= 300)
            return SERVER_ERROR_MES;

        var fcd = restAns.getResponse();
        if (fcd.getCause() != null)
            return fcd.getCause();

        return String.format("Параметры [%s] удалены", fcd.getTdpId());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.USER;
    }
}
