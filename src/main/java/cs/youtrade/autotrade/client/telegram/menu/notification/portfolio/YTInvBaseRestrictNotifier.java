package cs.youtrade.autotrade.client.telegram.menu.notification.portfolio;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.portfolio.YTInvBaseRestrictNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static cs.youtrade.autotrade.client.util.notification.YTNotificationType.PORTFOLIO_RESTRICTED;

@Component
public class YTInvBaseRestrictNotifier extends YTTextNotifier<YTInvBaseRestrictNotification> {
    public YTInvBaseRestrictNotifier(TelegramSendMessageService sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTInvBaseRestrictNotification data) {
        return String.format("""
                        %s <b>Предметы успешно %s</b>
                        <blockquote>• Количество: <b>%s</b>
                        • Аккаунт: <b>%s</b></blockquote>
                        """,
                DynamicEmoji.SUCCESS, getAction(data), data.getAmount(), data.getTokenName());
    }

    private String getAction(YTInvBaseRestrictNotification data) {
        return data.getType().equals(PORTFOLIO_RESTRICTED) ? "запрещены" : "разрешены";
    }
}
