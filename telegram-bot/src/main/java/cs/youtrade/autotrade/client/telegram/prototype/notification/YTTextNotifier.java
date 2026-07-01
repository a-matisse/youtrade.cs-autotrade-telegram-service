package cs.youtrade.autotrade.client.telegram.prototype.notification;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.notification.YTBaseNotification;
import cs.youtrade.telegram.buttons.def.message.DefaultMessageProcessor;
import cs.youtrade.telegram.buttons.def.message.DefaultOnErrorProcessor;
import cs.youtrade.telegram.buttons.def.message.MessageProcessor;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class YTTextNotifier<D extends YTBaseNotification> {
    private final UserTextMessageSender sender;
    private final TelegramClient bot;

    public void notify(UserData user, D data) {
        var builder = SendMessage
                .builder()
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true);
        // Добавление chatId
        long chatId = data.getChatId();
        builder.chatId(chatId);
        // Добавление текста
        if (shouldNotify(data)) {
            String text = getText(data);
            builder.text(text);
            // Сборка сообщения и отправка
            Supplier<SendMessage> mesSupplier = builder::build;
            MessageProcessor<UserData> mesConsumer = isReplacing()
                    ? getDefaultMessageProcessor(user)
                    : getDefaultMessageProcessor();
            sender.sendMessage(bot, user, mesSupplier, mesConsumer);
        }
    }

    public boolean shouldNotify(D data) {
        return true;
    }

    public boolean isReplacing() {
        return false;
    }

    private DefaultOnErrorProcessor<UserData> getDefaultMessageProcessor() {
        return DefaultOnErrorProcessor
                .<UserData>errorBuilder()
                .sender(sender)
                .build();
    }

    private DefaultMessageProcessor<UserData, EditMessageText> getDefaultMessageProcessor(UserData user) {
        return DefaultMessageProcessor
                .<UserData, EditMessageText>defaultBuilder()
                .sender(sender)
                .bot(bot)
                .user(user)
                .supportedEdit(EditMessageText.class)
                .build();
    }

    public abstract String getText(D data);
}
