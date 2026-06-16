package cs.youtrade.autotrade.client.telegram.menu.notification.general;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.general.YTWaitNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTWaitNotifier extends YTTextNotifier<YTWaitNotification> {
    public YTWaitNotifier(UserTextMessageSender sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTWaitNotification data) {
        return String.format("%s <i>Пожалуйста, подождите. Процесс может занять какое-то время...</i>",
                DynamicEmoji.WAIT.getEmoji());
    }

    @Override
    public boolean isReplacing() {
        return true;
    }
}
