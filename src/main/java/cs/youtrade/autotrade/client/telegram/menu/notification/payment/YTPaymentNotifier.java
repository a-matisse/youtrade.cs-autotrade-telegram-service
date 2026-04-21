package cs.youtrade.autotrade.client.telegram.menu.notification.payment;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.prototype.notification.YTTextNotifier;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.notification.YTPaymentNotification;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class YTPaymentNotifier extends YTTextNotifier<YTPaymentNotification> {
    public YTPaymentNotifier(TelegramSendMessageService sender, TelegramClient bot) {
        super(sender, bot);
    }

    @Override
    public String getText(YTPaymentNotification data) {
        return String.format("""
                        <b>%s</b>
                        
                        <blockquote><b>ID транзакции</b>
                        <tg-spoiler>%s</tg-spoiler>
                        
                        • Сумма: <b>%.2f %s</b>
                        • Тип: <b>%s</b>
                        • Назначение: <b>Пополнение баланса</b></blockquote>
                        
                        <i>Дата: %s</i>""",
                getPaymentHeader(data),
                data.getIdempotencyKey(),
                data.getAmount().doubleValue(),
                data.getCurrency(),
                data.getTopUpType(),
                data.getPaymentTime()
        );
    }

    private String getPaymentHeader(YTPaymentNotification data) {
        return data.getSuccessful()
                ? String.format("%s Платеж успешно выполнен",
                DynamicEmoji.SUCCESS.getEmoji())
                : String.format("%s Платеж не выполнен",
                DynamicEmoji.ERROR.getEmoji());
    }
}
