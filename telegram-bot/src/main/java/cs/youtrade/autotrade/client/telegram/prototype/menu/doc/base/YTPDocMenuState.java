package cs.youtrade.autotrade.client.telegram.prototype.menu.doc.base;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.ErrorMessageGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.IMenuEnum;
import cs.youtrade.telegram.buttons.menu.doc.AbstractDocMenuState;
import cs.youtrade.telegram.buttons.sender.doc.BaseDocMessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class YTPDocMenuState<C, MENU extends IMenuEnum> extends AbstractDocMenuState<C, UserData, UserMenu, MENU> {
    public YTPDocMenuState(
            BaseDocMessageSender<UserData> sender
    ) {
        super(sender);
    }

    @Override
    public void sendDefErrMes(TelegramClient bot, UserData userData) {
        sender.sendTextMes(bot, userData, ErrorMessageGenerator.getErrorText());
    }

    protected boolean hasRows(C content) {
        return true;
    }

    protected String getEmptyText() {
        return "<b>Данных пока нет</b>\n<blockquote>Таблица появится, когда здесь будут записи.</blockquote>";
    }

    @Override
    public SendDocument buildMessage(TelegramClient bot, Update update, UserData userData) {
        C content = getContent(userData);
        if (content == null) {
            sendDefErrMes(bot, userData);
            return null;
        }
        if (!hasRows(content)) {
            sender.sendTextMes(bot, userData, getEmptyText());
            return null;
        }

        InputFile document = getHeaderDoc(userData, content);
        if (document == null) {
            sendDefErrMes(bot, userData);
            return null;
        }
        String title = getHeaderText(bot, userData);
        if (title == null) {
            sendDefErrMes(bot, userData);
            return null;
        }
        String details = getHeaderDocText(userData, content);
        String caption = details == null || details.isBlank() ? title : title + "\n\n" + details;
        return SendDocument.builder()
                .chatId(userData.getChatId())
                .caption(caption)
                .document(document)
                .replyMarkup(buildMarkup(userData))
                .parseMode("html")
                .build();
    }

    @Override
    public EditMessageMedia buildEdit(TelegramClient bot, UserData userData) {
        C content = getContent(userData);
        if (content == null) {
            sendDefErrMes(bot, userData);
            return null;
        }
        if (!hasRows(content)) {
            sender.sendTextMes(bot, userData, getEmptyText());
            return null;
        }

        InputFile document = getHeaderDoc(userData, content);
        if (document == null) {
            sendDefErrMes(bot, userData);
            return null;
        }
        var media = InputMediaDocument.builder()
                .media(document.getNewMediaFile(), document.getMediaName());
        String caption = getHeaderDocText(userData, content);
        if (caption != null && !caption.isBlank()) {
            media.caption(caption).parseMode("html");
        }
        return EditMessageMedia.builder()
                .chatId(userData.getChatId())
                .messageId(userData.getLastMessageId())
                .replyMarkup(buildMarkup(userData))
                .media(media.build())
                .build();
    }
}
