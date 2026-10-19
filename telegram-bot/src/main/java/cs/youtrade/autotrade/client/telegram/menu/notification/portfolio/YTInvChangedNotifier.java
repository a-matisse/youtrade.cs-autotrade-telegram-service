package cs.youtrade.autotrade.client.telegram.menu.notification.portfolio;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.portfolio.YTChangeNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTInvChangedNotifier extends YTTextNotifier<YTChangeNotification> {
    public YTInvChangedNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public boolean shouldNotify(YTChangeNotification data) {
        return !data.getList().isEmpty();
    }

    @Override
    public String getText(UserData user, YTChangeNotification data) {
        var translations = botTexts.forUser(user, "notification.InventoryNotifications");
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s: <b>%s</b>
                        • %s: <b>%s</b></blockquote>
                        """,
                        DynamicEmoji.MONEY.getEmoji(),
                        translations.get("listed-items-updated"),
                        translations.get("quantity"),
                        data.getList().size(),
                        translations.get("account"),
                        data.getTokenName());
    }
}
