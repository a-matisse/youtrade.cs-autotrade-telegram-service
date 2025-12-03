package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.asswitch.evals1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class SwitchEvalS1 extends AbstractTerminalTextMenuState {
    private final SellDefaultEndpoint endpoint;

    public SwitchEvalS1(
            UserTextMessageSender sender,
            SellDefaultEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL_SWITCH_EVAL_MODE_S1;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var restAns = endpoint.switchEvalModeS1(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return fcd.getData()
                ? "✅ EvalModeS1 включён"
                : "❌ EvalModeS1 выключен";
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOSELL;
    }
}
