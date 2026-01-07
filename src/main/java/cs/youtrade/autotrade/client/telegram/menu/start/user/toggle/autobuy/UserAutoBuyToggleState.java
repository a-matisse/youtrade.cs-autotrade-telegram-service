package cs.youtrade.autotrade.client.telegram.menu.start.user.toggle.autobuy;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.BuyEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserAutoBuyToggleState extends AbstractTerminalTextMenuState {
    private final BuyEndpoint endpoint;

    public UserAutoBuyToggleState(
            UserTextMessageSender sender,
            BuyEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.USER_TOGGLE_AUTOBUY;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var restAns = endpoint.toggle(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return fcd.getData()
                ? "Бот покупки успешно запущен и работает"
                : "Бот покупки остановлен";
    }

    @Override
    public UserMenu retState() {
        return UserMenu.USER;
    }
}
