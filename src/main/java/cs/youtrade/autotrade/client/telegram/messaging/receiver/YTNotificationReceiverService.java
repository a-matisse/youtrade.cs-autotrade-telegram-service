package cs.youtrade.autotrade.client.telegram.messaging.receiver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.notification.buy.YTBuyCompletedNotifier;
import cs.youtrade.autotrade.client.telegram.menu.notification.buy.YTBuyFailedNotifier;
import cs.youtrade.autotrade.client.telegram.menu.notification.mafile.YTMaFileDeletedNotifier;
import cs.youtrade.autotrade.client.telegram.menu.notification.payment.YTPaymentNotifier;
import cs.youtrade.autotrade.client.telegram.menu.notification.sell.YTSellAddedNotifier;
import cs.youtrade.autotrade.client.telegram.menu.notification.sell.YTSellCompletedNotifier;
import cs.youtrade.autotrade.client.telegram.menu.notification.sell.YTSellFailedNotifier;
import cs.youtrade.autotrade.client.telegram.messaging.TelegramSendMessageService;
import cs.youtrade.autotrade.client.telegram.messaging.dto.UserStateData;
import cs.youtrade.autotrade.client.telegram.prototype.StateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.UserInitializer;
import cs.youtrade.autotrade.client.telegram.prototype.UserRegistry;
import cs.youtrade.autotrade.client.util.minio.MinIOFileDownloadService;
import cs.youtrade.autotrade.client.util.minio.dto.MinIODto;
import cs.youtrade.autotrade.client.util.minio.dto.MinIOInputStream;
import cs.youtrade.autotrade.client.util.notification.*;
import cs.youtrade.autotrade.client.util.notification.buy.YTBuyCompletedNotification;
import cs.youtrade.autotrade.client.util.notification.buy.YTBuyFailedNotification;
import cs.youtrade.autotrade.client.util.notification.mafile.YTMaFileDeleteNotification;
import cs.youtrade.autotrade.client.util.notification.sell.YTSellAddedNotification;
import cs.youtrade.autotrade.client.util.notification.sell.YTSellCompletedNotification;
import cs.youtrade.autotrade.client.util.notification.sell.YTSellFailedNotification;
import cs.youtrade.autotrade.client.util.redis.IRedisConsumer;
import cs.youtrade.ytrest.gson.GsonConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@RequiredArgsConstructor
@Log4j2
public class YTNotificationReceiverService implements IRedisConsumer<YTAnyNotification> {
    private static final Gson GSON = GsonConfig.createGson();
    private final MinIOFileDownloadService minIOFileDownloadService;
    private final TelegramSendMessageService sendMessage;
    private final TelegramClient bot;
    private final StateRegistry stateRegistry;
    private final UserRegistry userRegistry;
    // Вспомогательные сервисы уведомлений
    private final YTPaymentNotifier paymentNotifier;
    private final YTBuyCompletedNotifier buyCompletedNotifier;
    private final YTBuyFailedNotifier buyFailedNotifier;
    private final YTSellAddedNotifier sellAddedNotifier;
    private final YTSellCompletedNotifier sellCompletedNotifier;
    private final YTSellFailedNotifier sellFailedNotifier;
    private final YTMaFileDeletedNotifier maFileDeletedNotifier;
    private final UserInitializer userInitializer;

    @Override
    public boolean shouldDeserialize() {
        return false;
    }

    @Override
    public boolean consume(String payload, YTAnyNotification data) {
        JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
        ProductType productType = GSON.fromJson(json.get("product"), ProductType.class);
        if (productType != ProductType.YOUTRADE_PRO)
            return false;
        if (!json.has("chatId") || json.get("chatId").isJsonNull())
            return false;

        YTNotificationType notificationType = YTNotificationType.valueOf(json.get("type").getAsString());
        switch (notificationType) {
            case MESSAGE -> consumeMessage(GSON.fromJson(json, YTMessageNotification.class));
            case BALANCE -> consumeBalance(GSON.fromJson(json, YTBalanceNotification.class));
            case PAYMENT -> paymentNotifier.notify(GSON.fromJson(json, YTPaymentNotification.class));
            case BUY_COMPLETED -> buyCompletedNotifier.notify(GSON.fromJson(json, YTBuyCompletedNotification.class));
            case BUY_FAILED -> buyFailedNotifier.notify(GSON.fromJson(json, YTBuyFailedNotification.class));
            case SELL_ADDED -> sellAddedNotifier.notify(GSON.fromJson(json, YTSellAddedNotification.class));
            case SELL_COMPLETED -> sellCompletedNotifier.notify(GSON.fromJson(json, YTSellCompletedNotification.class));
            case SELL_FAILED -> sellFailedNotifier.notify(GSON.fromJson(json, YTSellFailedNotification.class));
            case MAFILE_DELETED -> maFileDeletedNotifier.notify(GSON.fromJson(json, YTMaFileDeleteNotification.class));
        }
        return true;
    }

    private void consumeMessage(YTMessageNotification data) {
        var type = YTMessageType.fromYouTradeNotification(data);
        switch (type) {
            case TEXT -> consumeText(data);
            case IMAGE -> consumeImage(data);
            case DOCUMENT -> consumeDocument(data);
            default -> consumeError(data);
        }
    }

    private void consumeBalance(YTBalanceNotification data) {
        // Получение состояния
        var user = userRegistry.getOrCreateUser(data.getChatId(), userInitializer::initUser);
        UserStateData stateData = stateRegistry.getState(user);
        // Выполнение алгоритма с учетом прошлого состояния
        var newState = UserMenu.NOTIFICATION_BALANCE;
        stateRegistry.getNotification(newState).executeOnState(bot, user, stateData, data);
        // Сохранение нового состояния
        stateData.setMenuState(newState);
        stateRegistry.put(user, stateData);
    }

    private void consumeText(YTMessageNotification data) {
        var builder = SendMessage
                .builder()
                .parseMode(ParseMode.HTML);
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
        sendMessage.sendMessage(bot, chatId, mes, null);
    }

    private void consumeImage(YTMessageNotification data) {
        var builder = SendPhoto.builder().parseMode(ParseMode.HTML);
        // Получение chatId
        long chatId = data.getChatId();
        builder.chatId(chatId);
        // Получение изображения
        var image = fetchAndDeleteFile(data.getImage());
        if (image == null) {
            log.error("[notification-receiver] Can't create SendPhoto: no image");
            consumeText(data);
            return;
        }
        builder.photo(image.getFile());
        // Получение текста
        String text = data.getText();
        if (text != null && !text.isBlank()) builder.caption(text);
        // Сборка сообщения и отправка
        var mes = builder.build();
        sendMessage.sendMessage(bot, chatId, mes, null);
    }

    private void consumeDocument(YTMessageNotification data) {
        var builder = SendDocument.builder().parseMode(ParseMode.HTML);
        // Получение chatId
        long chatId = data.getChatId();
        builder.chatId(chatId);
        // Получение документа
        var doc = fetchAndDeleteFile(data.getDocument());
        if (doc == null) {
            log.error("[notification-receiver] Can't create SendDocument: no doc");
            consumeImage(data);
            return;
        }
        builder.document(doc.getFile());
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
        sendMessage.sendMessage(bot, chatId, mes, null);
    }

    private void consumeError(YTMessageNotification data) {
        log.error("[notification-receiver] No data by td.id={} and td.chatId={}", data.getTdId(), data.getChatId());
    }

    private MinIOInputStream fetchAndDeleteFile(MinIODto dto) {
        return minIOFileDownloadService.fetchAndDeleteFile(dto);
    }
}
