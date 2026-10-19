package cs.youtrade.autotrade.client.telegram.menu.notification.sell;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import java.util.Map;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.sell.YTSellAddedNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTSellAddedNotifier extends YTTextNotifier<YTSellAddedNotification> {
    public YTSellAddedNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public String getText(UserData user, YTSellAddedNotification data) {
        var translations = botTexts.forUser(user, "notification.SellNotifications");
        return String.format("""
                        %s <b>%s</b>
                        
                        <code><b>%s</b></code>
                        <blockquote>%s %s
                        %s %s: <b>$%.2f</b>
                        <i>%s: <b>%s</b></i></blockquote>
                        
                        %s
                        """,
                        DynamicEmoji.LOADING.getEmoji(),
                        translations.get("listed-for-sale"),
                        data.getItemName(),
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.format("range-from-to", Map.of("value1", String.format("<b>$%.2f</b>", data.getMinPrice()), "value2", String.format("<b>$%.2f</b>", data.getMaxPrice()))),
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.get("purchase-price"),
                        data.getBuyPrice(),
                        translations.get("purchase-date"),
                        data.getBoughtAt(),
                        data.getTokenInfoStr(translations.inBundle("notification.SkinNotification")));
    }
}
