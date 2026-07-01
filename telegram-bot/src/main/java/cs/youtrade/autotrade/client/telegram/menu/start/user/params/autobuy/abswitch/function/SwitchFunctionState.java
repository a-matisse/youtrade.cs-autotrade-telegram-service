package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.abswitch.function;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.BuyEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class SwitchFunctionState extends YTPTerminalTextMenuState {
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

        return "<b>" + switch (fcd.getData()) {
            case LINEAR -> String.format("%s Линейная функция выбрана",
                    DynamicEmoji.GRAPH_UP.getEmoji());
            case EXPONENTIAL -> String.format("%s Экспоненциальная функция выбрана",
                    DynamicEmoji.GRAPH.getEmoji());
            case LOGARITHMIC -> String.format("%s Логарифмическая функция выбрана",
                    DynamicEmoji.GRAPH_DOWN.getEmoji());
            case PREDICTIVE -> String.format("%s Прогнозная функция выбрана",
                    DynamicEmoji.MYSTERY.getEmoji());
            case NONE -> String.format("%s Функция не задана",
                    DynamicEmoji.ERROR.getEmoji());
        } + "</b>";
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOBUY;
    }
}
