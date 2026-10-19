package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.abswitch.duplicate;

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
public class SwitchDuplicateState extends YTPTerminalTextMenuState {
    private final BuyEndpoint buyEndpoint;

    public SwitchDuplicateState(
            UserTextMessageSender sender,
            BuyEndpoint buyEndpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.buyEndpoint = buyEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_SWITCH_DUPLICATE_MODE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "parameters.autobuy.AutoBuyModes");
        var restAns = buyEndpoint.switchDuplicateMode(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return switch (fcd.getData()) {
            case NUMERIC -> String.format("%s <b>%s</b>",
                    DynamicEmoji.NUMERIC.getEmoji(),
                    translations.get("item-count-duplication-mode-selected"));
            case PERCENTAGE -> String.format("%s <b>%s</b>",
                    DynamicEmoji.GRAPH.getEmoji(),
                    translations.get("percentage-duplication-mode-selected"));
            case PERCENTAGE_PORTFOLIO -> String.format("%s <b>%s</b>",
                    DynamicEmoji.GRAPH.getEmoji(),
                    translations.get("percentage-of-capital-duplication-mode-selected"));
        };
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOBUY;
    }
}
