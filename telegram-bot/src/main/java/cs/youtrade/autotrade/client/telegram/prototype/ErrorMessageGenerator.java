package cs.youtrade.autotrade.client.telegram.prototype;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.telegram.buttons.data.AbstractUserData;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Log4j2
public class ErrorMessageGenerator {
    public static String getErrorText(LocalizedTexts translations) {
        return String.format("""
                🚫 <b>%s</b>
                
                %s
                """,
                translations.get("the-service-is-unavailable-or-the-application"),
                translations.get("send-any-command-for-example-start-to"));
    }

    public static <USER extends AbstractUserData> SendMessage generateMessage(USER user, LocalizedTexts translations) {
        return SendMessage
                .builder()
                .chatId(user.getChatId())
                .text(getErrorText(translations))
                .parseMode(ParseMode.HTML)
                .build();
    }
}
