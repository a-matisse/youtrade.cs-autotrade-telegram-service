package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.update.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.update.UserAutoBuyUpdateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AutoBuyUpdateProceedState extends AbstractTerminalTextMenuState {
    private final UserAutoBuyUpdateRegistry registry;
    private final GeneralEndpoint endpoint;

    public AutoBuyUpdateProceedState(
            UserTextMessageSender sender,
            UserAutoBuyUpdateRegistry registry,
            GeneralEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_UPDATE_FIELD_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var data = registry.remove(user);
        var restAns = endpoint.changeField(user.getChatId(), data.getField().getFName(), data.getValue());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("Поле %s успешно обновлено", data.getField().getFName());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOBUY;
    }
}
