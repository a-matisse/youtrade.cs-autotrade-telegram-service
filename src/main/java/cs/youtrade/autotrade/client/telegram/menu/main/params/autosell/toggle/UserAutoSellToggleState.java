package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.toggle;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import org.springframework.stereotype.Service;

@Service
public class UserAutoSellToggleState extends AbstractTerminalTextMenuState {
    private final SellDefaultEndpoint endpoint;

    public UserAutoSellToggleState(
            UserTextMessageSender sender,
            SellDefaultEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL_TOGGLE_AUTOSELL;
    }

    @Override
    public String getHeaderText(UserData user) {
        var restAns = endpoint.toggle(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return fcd.getData()
                ? "Бот продажи успешно запущен и работает"
                : "Бот продажи остановлен";
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOSELL;
    }
}
