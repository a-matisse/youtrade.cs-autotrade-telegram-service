package cs.youtrade.autotrade.client.telegram.menu.notification.buy;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.buy.YTBuyFailedNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTBuyFailedNotifier extends YTTextNotifier<YTBuyFailedNotification> {
    public YTBuyFailedNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public String getText(UserData user, YTBuyFailedNotification data) {
        var translations = botTexts.forUser(user, "notification.BuyNotifications");
        return String.format("""
                        %s <b>%s</b>
                        
                        <code><b>%s</b></code>
                        <blockquote>%s %s: <b>$%s</b>
                        %s %s: <b>%s</b></blockquote>
                        
                        %s
                        """,
                        DynamicEmoji.ERROR.getEmoji(),
                        translations.get("purchase-cancelled"),
                        data.getItemName(),
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.get("refunded"),
                        data.getRefunded().toPlainString(),
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.get("reason"),
                        data.getReason(),
                        data.getTokenInfoStr(translations.inBundle("notification.SkinNotification")));
    }
}
