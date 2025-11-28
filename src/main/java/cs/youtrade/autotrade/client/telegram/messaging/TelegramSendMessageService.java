package cs.youtrade.autotrade.client.telegram.messaging;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Log4j2
public class TelegramSendMessageService {
    protected static final BlockingQueue<MessageInfoDto> messageQueue = new LinkedBlockingQueue<>();
    protected static final long RATE_LIMIT_DELAY_MS = 35;
    protected static final int MAX_MESSAGE_LENGTH = 4096;

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private final Map<Long, Long> lastTimeSentMessages = new HashMap<>();

    @PostConstruct
    public void init() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(
                this::sendMessageFromQueue, 0,
                RATE_LIMIT_DELAY_MS, TimeUnit.MILLISECONDS
        );
    }

    public void deleteMes(TelegramClient bot, Long chatId, Update update) {
        try {
            int messageId = update.getMessage().getMessageId();
            DeleteMessage delete = DeleteMessage
                    .builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();
            bot.execute(delete);
        } catch (TelegramApiException e) {
            log.error("Couldn't delete message: {}", e.getMessage());
        }
    }

    public void deleteCallback(TelegramClient bot, Long chatId, Update update) {
        try {
            int callbackId = update.getCallbackQuery().getMessage().getMessageId();
            DeleteMessage delete = DeleteMessage
                    .builder()
                    .chatId(chatId)
                    .messageId(callbackId)
                    .build();
            bot.execute(delete);
        } catch (TelegramApiException e) {
            log.error("Couldn't delete callback: {}", e.getMessage());
        }
    }

    public InputStream downloadFile(TelegramClient bot, Document doc) throws TelegramApiException {
        GetFile gf = new GetFile(doc.getFileId());
        var tgFile = bot.execute(gf);
        return bot.downloadFileAsStream(tgFile);
    }

    public SendMessage createMessage(Long chatId, String text) throws TelegramApiException {
        if (text.length() > MAX_MESSAGE_LENGTH)
            throw new TelegramApiException(String.format("Too long message length (%s)", text.length()));

        return SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    /**
     * Готовит и отправляет сообщение(-я)
     *
     * @param chatId ID чата пользователя в Telegram
     * @param text   Текст сообщения ответа
     */
    public void sendMessage(
            TelegramClient bot,
            Long chatId,
            String text
    ) {
        SendMessage message;
        int startIndex = 0;

        do {
            int endIndex = Math.min(startIndex + MAX_MESSAGE_LENGTH, text.length());
            String chunk = text.substring(startIndex, endIndex);
            message = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(chunk)
                    .build();

            messageQueue.add(new MessageInfoDto(bot, message, chatId));
            startIndex = endIndex;
        } while (startIndex < text.length());
    }

    /**
     * Отправляет сообщение
     *
     * @param chatId  ID чата пользователя в Telegram
     * @param message Подготовленное текстовое сообщение ответа
     */
    public void sendMessage(
            TelegramClient bot,
            Long chatId,
            SendMessage message
    ) {
        messageQueue.add(new MessageInfoDto(bot, message, chatId));
    }

    /**
     * Отправляет текстовое сообщение
     *
     * @param chatId ID чата пользователя в Telegram
     * @param doc    Документ (возможна подпись)
     */
    public void sendMessage(
            TelegramClient bot,
            Long chatId,
            SendDocument doc
    ) {
        messageQueue.add(new MessageInfoDto(bot, doc, chatId));
    }

    /**
     * Отправляет текстовое сообщение
     *
     * @param chatId ID чата пользователя в Telegram
     * @param edit   Исправление сообщения
     */
    public void sendMessage(
            TelegramClient bot,
            Long chatId,
            EditMessageReplyMarkup edit
    ) {
        messageQueue.add(new MessageInfoDto(bot, edit, chatId));
    }

    public void sendMessage(
            TelegramClient bot,
            Long chatId,
            AnswerCallbackQuery ack
    ) {
        messageQueue.add(new MessageInfoDto(bot, ack, chatId));
    }

    private void sendMessageFromQueue() {
        long chatId = -1;
        long now = System.currentTimeMillis();

        try {
            MessageInfoDto messageInfo = messageQueue.take();
            chatId = messageInfo.getChatId();
            long lastTime = lastTimeSentMessages.getOrDefault(chatId, 0L);

            if (now - lastTime < 1020) {
                messageQueue.offer(messageInfo);
                return;
            }

            TelegramClient bot = messageInfo.getBot();
            switch (messageInfo.getMessageType()) {
                case TEXT -> bot.execute(messageInfo.getMessage());
                case DOCUMENT -> bot.execute(messageInfo.getDoc());
                case EDIT -> bot.execute(messageInfo.getReplyMarkup());
                case ANSWER_CALLBACK -> bot.execute(messageInfo.getAck());
            }
            lastTimeSentMessages.put(chatId, now);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Поток Rate Limiter был прерван {}", e.getMessage());
        } catch (TelegramApiException e) {
            if (chatId != -1002332618563L)
                log.error("Ошибка при отправке сообщения по id={}: {}", chatId, e.getMessage());
        }
    }
}
