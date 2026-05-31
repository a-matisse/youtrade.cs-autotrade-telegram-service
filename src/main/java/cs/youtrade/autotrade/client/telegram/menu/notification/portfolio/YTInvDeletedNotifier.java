package cs.youtrade.autotrade.client.telegram.menu.notification.portfolio;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.portfolio.YTDeleteNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTInvDeletedNotifier extends YTTextNotifier<YTDeleteNotification> {
    public YTInvDeletedNotifier(TelegramSendMessageService sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTDeleteNotification data) {
        return String.format("""
                        %s <b>Предметы сняты с продажи</b>
                        <blockquote>• Количество: <b>%s</b>
                        • Аккаунт: <b>%s</b></blockquote>
                        """,
                DynamicEmoji.OFF, data.getAns().getCount(), data.getAns().getGivenName());
    }
}
