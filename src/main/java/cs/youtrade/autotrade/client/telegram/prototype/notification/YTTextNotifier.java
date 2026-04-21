package cs.youtrade.autotrade.client.telegram.prototype.notification;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.util.notification.YTBaseNotification;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@RequiredArgsConstructor
public abstract class YTTextNotifier<D extends YTBaseNotification> {
    private final TelegramSendMessageService sender;
    private final TelegramClient bot;

    public void notify(D data) {
        var builder = SendMessage
                .builder()
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true);
        // Добавление chatId
        long chatId = data.getChatId();
        builder.chatId(chatId);
        // Добавление текста
        String text = getText(data);
        builder.text(text);
        // Сборка сообщения и отправка
        var mes = builder.build();
        sender.sendMessage(bot, chatId, mes, null);
    }

    public abstract String getText(D data);
}
