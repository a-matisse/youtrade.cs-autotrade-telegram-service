package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserLanguage;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Log4j2
public class UserLanguageState extends YTPTextMenuState<UserLanguageMenu> {
    private final GeneralEndpoint endpoint;

    public UserLanguageState(UserTextMessageSender sender, GeneralEndpoint endpoint) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.LANGUAGE;
    }

    @Override
    public UserLanguageMenu getOption(String value) {
        return UserLanguageMenu.valueOf(value);
    }

    @Override
    public UserLanguageMenu[] getOptions(UserData user) {
        return UserLanguageMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, UserLanguageMenu option) {
        if (option == UserLanguageMenu.RETURN) return UserMenu.START;

        UserLanguage language = option == UserLanguageMenu.RU ? UserLanguage.RU : UserLanguage.EN;
        try {
            var answer = endpoint.setLanguage(user.getChatId(), language.getCode());
            if (answer.getStatus() < 300) {
                user.setLanguage(language);
                return supportedState();
            }
            log.warn("Couldn't change language for chat {}: HTTP {}", user.getChatId(), answer.getStatus());
        } catch (RuntimeException e) {
            log.warn("Couldn't change language for chat {}: {}", user.getChatId(), e.getMessage());
        }
        sender.sendTextMes(bot, user, "Не удалось сменить язык. Попробуйте позже.");
        return supportedState();
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s <i>Язык</i>

                %s <b>Язык бота</b>
                <blockquote>• Сейчас: <b>%s</b></blockquote>
                └ <i>Выберите язык интерфейса.</i>
                """,
                DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.SETTINGS.getEmoji(),
                user.effectiveLanguage().getName());
    }
}
