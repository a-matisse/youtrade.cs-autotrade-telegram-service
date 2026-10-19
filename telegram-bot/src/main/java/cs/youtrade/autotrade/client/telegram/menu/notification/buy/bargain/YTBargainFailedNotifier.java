package cs.youtrade.autotrade.client.telegram.menu.notification.buy.bargain;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.buy.YTBargainNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTBargainFailedNotifier extends YTTextNotifier<YTBargainNotification> {
    public YTBargainFailedNotifier(
            UserTextMessageSender sender,
            TelegramClient bot,
            BotTexts botTexts
    ) {
        super(sender, bot, botTexts);
    }

    @Override
    public String getText(UserData user, YTBargainNotification data) {
        var translations = botTexts.forUser(user, "notification.BargainNotifications");
        return String.format("""
                        %s <b>%s</b>
                        
                        <code><b>%s</b></code>
                        <blockquote>%s %s: <b>$%.2f</b>
                        %s %s: <b>%.2f%%</b></blockquote>
                        
                        %s
                        """,
                        DynamicEmoji.OFF.getEmoji(),
                        translations.get("bargain-failed"),
                        data.getItemName(),
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.get("bought-for"),
                        data.getPrice(),
                        DynamicEmoji.BULLET_YELLOW.getEmoji(),
                        translations.get("discount"),
                        data.getAdditionalProfit(),
                        data.getTokenInfoStr(translations.inBundle("notification.SkinNotification")));
    }
}
