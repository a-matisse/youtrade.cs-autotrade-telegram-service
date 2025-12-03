package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.abswitch.function;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.BuyEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class SwitchFunctionState extends AbstractTerminalTextMenuState {
    private final BuyEndpoint buyEndpoint;

    public SwitchFunctionState(
            UserTextMessageSender sender,
            BuyEndpoint buyEndpoint
    ) {
        super(sender);
        this.buyEndpoint = buyEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_SWITCH_FUNCTION_TYPE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var restAns = buyEndpoint.switchFunctionType(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return switch (fcd.getData()) {
            case LINEAR -> "📈 Линейная функция выбрана.";
            case EXPONENTIAL -> "📊 Экспоненциальная функция выбрана.";
            case LOGARITHMIC -> "📉 Логарифмическая функция выбрана.";
            case PREDICTIVE -> "🔮 Прогнозная функция выбрана.";
            case NONE -> "❌ Функция не задана.";
        };
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOBUY;
    }
}
