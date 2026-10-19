package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.asswitch.evalmode;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class SwitchEvalModeState extends YTPTerminalTextMenuState {
    private final SellDefaultEndpoint endpoint;

    public SwitchEvalModeState(
            UserTextMessageSender sender,
            SellDefaultEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL_SWITCH_EVAL_MODE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "parameters.autosell.UserAutoSell");
        var restAns = endpoint.switchEvalMode(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return switch (fcd.getData()) {
            case DEFAULT -> String.format("%s <b>%s</b>",
                    DynamicEmoji.SETTINGS.getEmoji(),
                    translations.get("standard-selling-price-valuation-selected"));
            case INTELLIGENT_V1 -> String.format("%s <b>%s</b>",
                    DynamicEmoji.INTELLIGENT.getEmoji(),
                    translations.get("intelligent-v1-selling-price-valuation-selected"));
        };
    }

    @Override
    public UserMenu retState() {
        return UserMenu.AUTOSELL;
    }
}
