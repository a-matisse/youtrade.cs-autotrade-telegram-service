package cs.youtrade.autotrade.client.telegram.messaging.receiver;

import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.util.minio.MinIOFileDownloadService;
import cs.youtrade.autotrade.client.util.minio.dto.MinIODto;
import cs.youtrade.autotrade.client.util.notification.YouTradeNotification;
import cs.youtrade.autotrade.client.util.notification.YouTradeNotificationType;
import cs.youtrade.autotrade.client.util.redis.IRedisConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationReceiverService implements IRedisConsumer<YouTradeNotification> {
    private final MinIOFileDownloadService minIOFileDownloadService;
    private final TelegramSendMessageService sendMessage;
    private final TelegramClient bot;

    @Override
    public void consume(YouTradeNotification data) {
        var type = YouTradeNotificationType.fromYouTradeNotification(data);
        switch (type) {
            case TEXT -> consumeText(data);
            case IMAGE -> consumeImage(data);
            case DOCUMENT -> consumeDocument(data);
            default -> consumeError(data);
        }
    }

    private void consumeText(YouTradeNotification data) {
        var builder = SendMessage.builder().parseMode(ParseMode.HTML);
        // Добавление chatId
        long chatId = data.getChatId();
        builder.chatId(chatId);
        // Добавление текста
        String text = data.getText();
        if (text == null) {
            log.error("[notification-receiver] Can't create SendMessage without text");
            consumeError(data);
            return;
        }
        builder.text(text);
        // Сборка сообщения и отправка
        var mes = builder.build();
        sendMessage.sendMessage(bot, chatId, mes);
    }

    private void consumeImage(YouTradeNotification data) {
        var builder = SendPhoto.builder().parseMode(ParseMode.HTML);
        // Получение chatId
        long chatId = data.getChatId();
        builder.chatId(chatId);
        // Получение изображения
        var image = fetchAndDeleteFile(data.getImage());
        if (image == null) {
            log.error("[notification-receiver] Can't create SendPhoto without image");
            consumeText(data);
            return;
        }
        builder.photo(image);
        // Получение текста
        String text = data.getText();
        if (text != null && !text.isBlank()) builder.caption(text);
        // Сборка сообщения и отправка
        var mes = builder.build();
        sendMessage.sendMessage(bot, chatId, mes);
    }

    private void consumeDocument(YouTradeNotification data) {
        var builder = SendDocument.builder().parseMode(ParseMode.HTML);
        // Получение chatId
        long chatId = data.getChatId();
        builder.chatId(chatId);
        // Получение документа
        var doc = fetchAndDeleteFile(data.getDocument());
        if (doc == null) {
            log.error("[notification-receiver] Can't create SendDocument without doc");
            consumeImage(data);
            return;
        }
        builder.document(doc);
        // Получение текста
        String text = data.getText();
        if (text != null && !text.isBlank()) builder.caption(text);
        // Получение изображения
        if (data.getImage() != null) {
            data.setText("");
            consumeImage(data);
        }
        // Сборка сообщения и отправка
        var mes = builder.build();
        sendMessage.sendMessage(bot, chatId, mes);
    }

    private void consumeError(YouTradeNotification data) {
        log.error("[notification-receiver] No data by td.id={} and td.chatId={}", data.getTdId(), data.getChatId());
    }

    private InputFile fetchAndDeleteFile(MinIODto dto) {
        return minIOFileDownloadService.fetchAndDeleteFile(dto);
    }
}
