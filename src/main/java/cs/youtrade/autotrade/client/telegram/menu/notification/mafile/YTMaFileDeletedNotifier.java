package cs.youtrade.autotrade.client.telegram.menu.notification.mafile;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.mafile.YTMaFileDeleteNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTMaFileDeletedNotifier extends YTTextNotifier<YTMaFileDeleteNotification> {
    public YTMaFileDeletedNotifier(UserTextMessageSender sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTMaFileDeleteNotification data) {
        return String.format("""
                        %s <b>Удален maFile связанный с аккаунтом <code>%s</code></b>
                        <blockquote>• Причина: <b>%s</b></blockquote>
                        """,
                DynamicEmoji.ERROR.getEmoji(),
                data.getGivenName(),
                data.getReason()
        );
    }
}
