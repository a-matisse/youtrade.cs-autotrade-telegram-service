package cs.youtrade.autotrade.client.telegram.menu.notification.mafile;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;

import java.util.Map;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.mafile.YTMaFileDeleteNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTMaFileDeletedNotifier extends YTTextNotifier<YTMaFileDeleteNotification> {
    public YTMaFileDeletedNotifier(UserTextMessageSender sender, TelegramClient bot, BotTexts botTexts) {
        super(sender, bot, botTexts);
    }

    @Override
    public String getText(UserData user, YTMaFileDeleteNotification data) {
        var translations = botTexts.forUser(user, "notification.YTMaFileDeleted");
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s: <b>%s</b></blockquote>
                        """,
                        DynamicEmoji.ERROR.getEmoji(),
                        translations.format("mafile-deleted-for-account", Map.of("value1", String.format("<code>%s</code>", data.getGivenName()))),
                        translations.get("reason"),
                        data.getReason());
    }
}
