package cs.youtrade.autotrade.client.telegram.menu.main.pswitch.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.pswitch.ParamsSwitchRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;

@Service
public class ParamsSwitchProceedState extends AbstractTerminalTextMenuState {
    private final ParamsSwitchRegistry registry;
    private final ParamsEndpoint endpoint;

    public ParamsSwitchProceedState(
            UserTextMessageSender sender,
            ParamsSwitchRegistry registry,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.MAIN_PARAMETERS_SWITCH_STAGE_2;
    }

    @Override
    public String getHeaderText(UserData userData) {
        var data = registry.remove(userData);
        var restAns = endpoint.switchP(userData.getChatId(), data.getInput());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return String.format("Текущие параметры переключены NAME=%s", fcd.getData().getGivenName());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.MAIN;
    }
}
