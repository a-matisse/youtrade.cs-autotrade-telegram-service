package cs.youtrade.autotrade.client.telegram.menu.notification.sell;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.sell.YTSellFailedNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTSellFailedNotifier extends YTTextNotifier<YTSellFailedNotification> {
    public YTSellFailedNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public String getText(UserData user, YTSellFailedNotification data) {
        var translations = botTexts.forUser(user, "notification.SellNotifications");
        return String.format("""
                        %s <b>%s</b>
                        
                        <code><b>%s</b></code>
                        <blockquote>%s %s: <b>$%s</b>
                        %s %s: <b>$%.2f</b>
                        <i>%s: <b>%s</b></i></blockquote>
                        
                        %s
                        """,
                        DynamicEmoji.ORANGE.getEmoji(),
                        translations.get("sell-order-cancelled"),
                        data.getItemName(),
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.get("refunded"),
                        data.getRefunded(),
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.get("selling-price"),
                        data.getSoldFor(),
                        translations.get("purchase-date"),
                        data.getPurchasedAt(),
                        data.getTokenInfoStr(translations.inBundle("notification.SkinNotification")));
    }
}
