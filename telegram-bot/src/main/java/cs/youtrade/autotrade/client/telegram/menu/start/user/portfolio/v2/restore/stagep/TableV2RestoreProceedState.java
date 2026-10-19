package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.restore.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.v2.SellV2RestoreEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableV2RestoreProceedState extends YTPTerminalTextMenuState {
    private final SellV2RestoreEndpoint endpoint;

    public TableV2RestoreProceedState(
            UserTextMessageSender sender,
            SellV2RestoreEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PORTFOLIO;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_V2_RESTORE_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "portfolio.TableV2Restore");
        var restAns = endpoint.restore(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("""
                        %s <b>%s</b>
                        └ <i>%s</i>
                        """,
                        DynamicEmoji.SUCCESS.getEmoji(),
                        translations.get("item-restoration-on-the-selling-marketplace-started"),
                        translations.get("results-will-arrive-in-separate-messages"));
    }
}
