package cs.youtrade.autotrade.client.telegram.menu.notification.payment;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.util.notification.YTPaymentNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
public class YTPaymentNotifier {
    private final TelegramSendMessageService sendMessage;
    private final TelegramClient bot;

    public void notify(YTPaymentNotification data) {
        var builder = SendMessage.builder().parseMode(ParseMode.HTML);
        // Добавление chatId
        long chatId = data.getChatId();
        builder.chatId(chatId);
        // Добавление текста
        String text = getNotificationText(data);
        builder.text(text);
        // Сборка сообщения и отправка
        var mes = builder.build();
        sendMessage.sendMessage(bot, chatId, mes, null);
    }

    private String getNotificationText(YTPaymentNotification data) {
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
                ? "✅ Платеж успешно выполнен"
                : "❌ Платеж не выполнен";
    }
}
