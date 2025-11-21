package cs.youtrade.autotrade.client.telegram.messaging;

import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Data
public class MessageInfoDto {
    private final MessageType messageType;
    private final TelegramClient bot;
    private final long chatId;
    private SendMessage message;
    private SendDocument doc;
    private EditMessageReplyMarkup replyMarkup;
    private AnswerCallbackQuery ack;

    public MessageInfoDto(TelegramClient bot, SendMessage message, long chatId) {
        this.bot = bot;
        this.message = message;
        this.chatId = chatId;
        this.messageType = MessageType.TEXT;
    }

    public MessageInfoDto(TelegramClient bot, SendDocument doc, long chatId) {
        this.bot = bot;
        this.doc = doc;
        this.chatId = chatId;
        this.messageType = MessageType.DOCUMENT;
    }

    public MessageInfoDto(TelegramClient bot, EditMessageReplyMarkup edit, long chatId) {
        this.bot = bot;
        this.replyMarkup = edit;
        this.chatId = chatId;
        this.messageType = MessageType.EDIT;
    }

    public MessageInfoDto(TelegramClient bot, AnswerCallbackQuery ack, long chatId) {
        this.bot = bot;
        this.ack = ack;
        this.chatId = chatId;
        this.messageType = MessageType.ANSWER_CALLBACK;
    }

    public boolean hasNullMessage() {
        return message == null;
    }

    public boolean hasNullDoc() {
        return doc == null;
    }
}
