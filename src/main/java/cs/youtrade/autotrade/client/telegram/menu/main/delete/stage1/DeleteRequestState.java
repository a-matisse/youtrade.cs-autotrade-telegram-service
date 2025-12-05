package cs.youtrade.autotrade.client.telegram.menu.main.delete.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.delete.ParamsDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.main.delete.ParamsDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsDeleteReqDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class DeleteRequestState extends AbstractTextMenuState<DeleteRequestMenu> {
    private final ParamsDeleteRegistry registry;
    private final ParamsEndpoint paramsEndpoint;

    public DeleteRequestState(
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
        return UserMenu.MAIN_PARAMETERS_DELETE_STAGE_1;
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
    public String getHeaderText(TelegramClient bot, UserData userData) {
        RestAnswer<FcdParamsDeleteReqDto> restAns = paramsEndpoint.requestDelete(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return SERVER_ERROR_MES;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        ParamsDeleteData deleteData = registry.getOrCreate(userData, ParamsDeleteData::new);
        deleteData.setConfirm(fcd.getConfirm());
        deleteData.setDecline(fcd.getDecline());

        return getUserAlertMes(fcd);
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, DeleteRequestMenu t) {
        ParamsDeleteData deleteData = registry.getOrCreate(userData, ParamsDeleteData::new);
        return switch (t) {
            case DELETE_CONFIRM -> {
                deleteData.setDecision(true);
                yield UserMenu.MAIN_PARAMETERS_DELETE_STAGE_P;
            }
            case DELETE_DECLINE -> {
                deleteData.setDecision(false);
                yield UserMenu.MAIN_PARAMETERS_DELETE_STAGE_P;
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
