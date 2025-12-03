package cs.youtrade.autotrade.client.telegram.prototype.menu.text;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.AbstractMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstractTextMenuState<MENU_TYPE extends IMenuEnum>
        extends AbstractMenuState<MENU_TYPE, SendMessage> {
    public AbstractTextMenuState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    public SendMessage buildMessage(TelegramClient bot, UserData userData) {
        String ans = "";
        try {
            String header = getHeaderText(bot, userData);
            if (header != null)
                ans = header;
        } catch (Exception ignored) {
        }
        if (ans.isEmpty())
            ans = "Не удалось обработать сообщение";

        return SendMessage
                .builder()
                .text(ans)
                .chatId(userData.getChatId())
                .replyMarkup(buildMarkup())
                .parseMode(ParseMode.HTML)
                .build();
    }
}
