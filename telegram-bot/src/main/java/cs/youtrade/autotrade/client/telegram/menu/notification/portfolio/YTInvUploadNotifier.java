package cs.youtrade.autotrade.client.telegram.menu.notification.portfolio;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.portfolio.YTInvUploadNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTInvUploadNotifier extends YTTextNotifier<YTInvUploadNotification> {
    public YTInvUploadNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public boolean shouldNotify(YTInvUploadNotification data) {
        return data.getInfo().getCount() > 0;
    }

    @Override
    public String getText(UserData user, YTInvUploadNotification data) {
        var translations = botTexts.forUser(user, "notification.InventoryNotifications");
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s: <b>%s</b>
                        • %s: <b>%s</b></blockquote>
                        """,
                        DynamicEmoji.ITEM_SEND.getEmoji(),
                        translations.get("items-listed-for-sale"),
                        translations.get("quantity"),
                        data.getInfo().getCount(),
                        translations.get("account"),
                        data.getInfo().getTokenName());
    }
}
