package cs.youtrade.autotrade.client.telegram.menu.notification.portfolio;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.portfolio.YTChangeNotification;
import cs.youtrade.autotrade.client.util.notification.portfolio.YTInvBaseRestrictNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTInvChangedNotifier extends YTTextNotifier<YTChangeNotification> {
    public YTInvChangedNotifier(TelegramSendMessageService sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public boolean shouldNotify(YTChangeNotification data) {
        return !data.getList().isEmpty();
    }

    @Override
    public String getText(YTChangeNotification data) {
        return String.format("""
                        %s <b>Предметы на продаже изменены</b>
                        <blockquote>• Количество: <b>%s</b>
                        • Аккаунт: <b>%s</b></blockquote>
                        """,
                DynamicEmoji.MONEY.getEmoji(), data.getList().size(), data.getTokenName());
    }
}
