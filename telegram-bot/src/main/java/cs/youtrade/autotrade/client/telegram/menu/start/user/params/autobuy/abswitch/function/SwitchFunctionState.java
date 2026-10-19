package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.abswitch.function;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
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
            BuyEndpoint buyEndpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.buyEndpoint = buyEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_SWITCH_FUNCTION_TYPE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.autobuy.AutoBuyModes");
        var restAns = buyEndpoint.switchFunctionType(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return "<b>" + switch (fcd.getData()) {
            case LINEAR -> String.format("%s %s",
                    DynamicEmoji.GRAPH_UP.getEmoji(),
                    translations.get("linear-function-selected"));
            case EXPONENTIAL -> String.format("%s %s",
                    DynamicEmoji.GRAPH.getEmoji(),
                    translations.get("exponential-function-selected"));
            case LOGARITHMIC -> String.format("%s %s",
                    DynamicEmoji.GRAPH_DOWN.getEmoji(),
                    translations.get("logarithmic-function-selected"));
            case PREDICTIVE -> String.format("%s %s",
                    DynamicEmoji.MYSTERY.getEmoji(),
                    translations.get("predictive-function-selected"));
            case NONE -> String.format("%s %s",
                    DynamicEmoji.ERROR.getEmoji(),
                    translations.get("no-function-set"));
        } + "</b>";
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOBUY;
    }
}
