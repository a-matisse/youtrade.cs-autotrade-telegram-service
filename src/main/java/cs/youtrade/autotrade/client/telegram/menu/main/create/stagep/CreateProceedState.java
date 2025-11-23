package cs.youtrade.autotrade.client.telegram.menu.main.create.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.create.ParamsCreateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.communication.RestAnswer;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;

@Service
public class CreateProceedState extends AbstractTerminalTextMenuState {
    private final ParamsCreateRegistry registry;
    private final ParamsEndpoint endpoint;

    public CreateProceedState(
            UserTextMessageSender sender,
            ParamsCreateRegistry registry,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_PARAMETERS_CREATE_STAGE_P;
    }

    @Override
    public String getHeaderText(UserData userData) {
        var data = registry.remove(userData);
        RestAnswer<FcdDefaultDto<Long>> restAns = endpoint.create(userData.getChatId(), data.getSource(), data.getDestination(), "");
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("Новые параметры созданы (params-ID=%d)", fcd.getData());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.MAIN;
    }
}
