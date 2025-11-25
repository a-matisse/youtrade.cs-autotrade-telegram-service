package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.asswitch.evalmode;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import org.springframework.stereotype.Service;

@Service
public class SwitchEvalModeState extends AbstractTerminalTextMenuState {
    private final SellDefaultEndpoint endpoint;

    public SwitchEvalModeState(
            UserTextMessageSender sender,
            SellDefaultEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL_SWITCH_EVAL_MODE;
    }

    @Override
    public String getHeaderText(UserData user) {
        var restAns = endpoint.switchEvalMode(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return switch (fcd.getData()) {
            case DEFAULT -> "⚙️ Выбран стандартный режим оценки цены продажи.";
            case INTELLIGENT_V1 -> "🧠 Выбран режим \"Intelligent V1\" для оценки цены продажи.";
        };
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOSELL;
    }
}
