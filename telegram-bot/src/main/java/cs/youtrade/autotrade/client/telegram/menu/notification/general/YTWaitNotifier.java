package cs.youtrade.autotrade.client.telegram.menu.notification.general;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.general.YTWaitNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTWaitNotifier extends YTTextNotifier<YTWaitNotification> {
    public YTWaitNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public String getText(UserData user, YTWaitNotification data) {
        var translations = botTexts.forUser(user, "notification.AccessNotifications");
        return String.format("%s <i>%s</i>",
                DynamicEmoji.WAIT.getEmoji(),
                translations.get("please-wait-this-may-take-some-time"));
    }

    @Override
    public boolean isReplacing() {
        return true;
    }
}
