package cs.youtrade.autotrade.client.telegram.menu.notification.sell;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.sell.YTSellCompletedNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;

@Component
public class YTSellCompletedNotifier extends YTTextNotifier<YTSellCompletedNotification> {
    public YTSellCompletedNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public String getText(UserData user, YTSellCompletedNotification data) {
        var translations = botTexts.forUser(user, "notification.SellNotifications");
        BigDecimal profit = data.getProfit();
        String emojiColor = profit.compareTo(BigDecimal.ZERO) >= 0
                ? DynamicEmoji.ON.getEmoji()
                : DynamicEmoji.OFF.getEmoji();
        return String.format("""
                        %s <b>%s</b>
                        
                        <code><b>%s</b></code>
                        <blockquote>%s %s: <b>%.2f%%</b>
                        %s %s: <b>$%.2f</b>
                        %s %s: <b>$%.2f</b>
                        <i>%s: <b>%s</b></i></blockquote>
                        
                        %s
                        """,
                        emojiColor,
                        translations.get("sell-order-completed"),
                        data.getItemName(),
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.get("profit"),
                        profit,
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.get("bought-for"),
                        data.getBoughtFor(),
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.get("sold-for"),
                        data.getSoldFor(),
                        translations.get("purchase-date"),
                        data.getPurchasedAt(),
                        data.getTokenInfoStr(translations.inBundle("notification.SkinNotification")));
    }
}
