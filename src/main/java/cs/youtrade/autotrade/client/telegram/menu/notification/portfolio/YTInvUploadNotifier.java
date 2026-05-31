package cs.youtrade.autotrade.client.telegram.menu.notification.portfolio;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.portfolio.YTInvUploadNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTInvUploadNotifier extends YTTextNotifier<YTInvUploadNotification> {
    public YTInvUploadNotifier(TelegramSendMessageService sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTInvUploadNotification data) {
        return String.format("""
                        %s <b>Предметы выставлены на продажу</b>
                        <blockquote>• Количество: <b>%s</b>
                        • Аккаунт: <b>%s</b></blockquote>
                        """,
                DynamicEmoji.ITEM_SEND, data.getInfo().getCount(), data.getInfo().getTokenName());
    }
}
