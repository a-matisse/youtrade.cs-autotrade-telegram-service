package cs.youtrade.autotrade.client.telegram.menu.notification.portfolio;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.portfolio.YTDeleteNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTInvDeletedNotifier extends YTTextNotifier<YTDeleteNotification> {
    public YTInvDeletedNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public boolean shouldNotify(YTDeleteNotification data) {
        return data.getAns().getCount() > 0;
    }

    @Override
    public String getText(UserData user, YTDeleteNotification data) {
        var translations = botTexts.forUser(user, "notification.InventoryNotifications");
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s: <b>%s</b>
                        • %s: <b>%s</b></blockquote>
                        """,
                        DynamicEmoji.OFF.getEmoji(),
                        translations.get("items-removed-from-sale"),
                        translations.get("quantity"),
                        data.getAns().getCount(),
                        translations.get("account"),
                        data.getAns().getGivenName());
    }
}
