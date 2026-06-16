package cs.youtrade.autotrade.client.telegram.menu.notification.portfolio;

import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.portfolio.YTInvUploadNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTInvUploadNotifier extends YTTextNotifier<YTInvUploadNotification> {
    public YTInvUploadNotifier(UserTextMessageSender sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public boolean shouldNotify(YTInvUploadNotification data) {
        return data.getInfo().getCount() > 0;
    }

    @Override
    public String getText(YTInvUploadNotification data) {
        return String.format("""
                        %s <b>Предметы выставлены на продажу</b>
                        <blockquote>• Количество: <b>%s</b>
                        • Аккаунт: <b>%s</b></blockquote>
                        """,
                DynamicEmoji.ITEM_SEND.getEmoji(), data.getInfo().getCount(), data.getInfo().getTokenName());
    }
}
