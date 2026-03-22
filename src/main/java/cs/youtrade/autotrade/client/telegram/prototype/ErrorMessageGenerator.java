package cs.youtrade.autotrade.client.telegram.prototype;

import cs.youtrade.telegram.buttons.data.AbstractUserData;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Log4j2
public class ErrorMessageGenerator {
    public static String getErrorText() {
        return """
                🚫 <b>Сервис недоступен или приложение было обновлено</b>
                
                Для синхронизации с текущей версией отправьте любую команду (например /start)
                """;
    }

    public static <USER extends AbstractUserData> SendMessage generateMessage(USER user) {
        return SendMessage
                .builder()
                .chatId(user.getChatId())
                .text(getErrorText())
                .parseMode(ParseMode.HTML)
                .build();
    }
}
