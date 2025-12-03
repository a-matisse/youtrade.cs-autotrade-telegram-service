package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.abswitch.duplicate;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.BuyEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class SwitchDuplicateState extends AbstractTerminalTextMenuState {
    private final BuyEndpoint buyEndpoint;

    public SwitchDuplicateState(
            UserTextMessageSender sender,
            BuyEndpoint buyEndpoint
    ) {
        super(sender);
        this.buyEndpoint = buyEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_SWITCH_DUPLICATE_MODE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var restAns = buyEndpoint.switchDuplicateMode(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return switch (fcd.getData()) {
            case NUMERIC -> "🔢 Выбран числовой режим дублирования.";
            case PERCENTAGE -> "📊 Выбран процентный режим дублирования.";
        };
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOBUY;
    }
}
