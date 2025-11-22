package cs.youtrade.autotrade.client.telegram.menu.main.mdelete.drequest;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.mdelete.ParamsDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.main.mdelete.ParamsDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsDeleteReqDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class DeleteRequestState extends AbstractTextMenuState<DeleteRequestMenu> {
    private final ParamsDeleteRegistry registry;
    private final ParamsEndpoint paramsEndpoint;

    public DeleteRequestState(
            TelegramSendMessageService sender,
            ParamsDeleteRegistry registry,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.registry = registry;
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_PARAMETERS_DELETE_REQUEST;
    }

    @Override
    public DeleteRequestMenu getOption(String optionStr) {
        return DeleteRequestMenu.valueOf(optionStr);
    }

    @Override
    public DeleteRequestMenu[] getOptions() {
        return DeleteRequestMenu.values();
    }

    @Override
    public String getHeaderText(UserData userData) {
        RestAnswer<FcdParamsDeleteReqDto> restAns = paramsEndpoint.requestDelete(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return SERVER_ERROR_MES;

        var fcd = restAns.getResponse();
        if (fcd.getCause() != null)
            return fcd.getCause();

        ParamsDeleteData deleteData = registry.getOrCreateBuilder(userData, ParamsDeleteData::new);
        deleteData.setConfirm(fcd.getConfirm());
        deleteData.setDecline(fcd.getDecline());

        return getUserAlertMes(fcd);
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, DeleteRequestMenu t) {
        ParamsDeleteData deleteData = registry.getOrCreateBuilder(userData, ParamsDeleteData::new);
        return switch (t) {
            case DELETE_CONFIRM -> {
                deleteData.setDecision(true);
                yield UserMenu.MAIN_PARAMETERS_DELETE_PROCEED;
            }
            case DELETE_DECLINE -> {
                deleteData.setDecision(false);
                yield UserMenu.MAIN_PARAMETERS_DELETE_PROCEED;
            }
        };
    }

    private String getUserAlertMes(FcdParamsDeleteReqDto fcd) {
        return String.format("""
                        Вы уверены, что хотите удалить текущие параметры [%s]? Это действие необратимо.
                        
                        ! ВНИМАНИЕ ! Если вы переключите параметры и нажмете "Удалить", то удалятся параметры, на которые вы переключили.
                        """,
                fcd.getTdpGivenName()
        );
    }
}
