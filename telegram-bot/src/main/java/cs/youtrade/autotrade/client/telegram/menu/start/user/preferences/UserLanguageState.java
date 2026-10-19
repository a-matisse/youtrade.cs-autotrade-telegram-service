package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.messaging.BotCommandProvider;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.function.Function;

@Service
@Log4j2
public class UserLanguageState extends YTPTextMenuState<UserLanguageMenu> {
    private static final String TEXT_BUNDLE = "preferences.UserLanguage";
    private final GeneralEndpoint endpoint;
    private final BotTexts texts;
    private final BotCommandProvider commands;

    public UserLanguageState(UserTextMessageSender sender, GeneralEndpoint endpoint,
                             BotTexts texts, BotCommandProvider commands) {
        super(sender, texts);
        this.endpoint = endpoint;
        this.texts = texts;
        this.commands = commands;
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
    public Map<UserLanguageMenu, Function<UserData, String>> getTextFunctions(UserData user) {
        return Map.of(UserLanguageMenu.RETURN, u -> UserLanguageMenu.RETURN
                .getButtonName(texts.forUser(u, TEXT_BUNDLE).get("back")));
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, UserLanguageMenu option) {
        if (option == UserLanguageMenu.RETURN) return UserMenu.START;

        UserLanguage language = option == UserLanguageMenu.RU ? UserLanguage.RU : UserLanguage.EN;
        try {
            var answer = endpoint.setLanguage(user.getChatId(), language.getCode());
            if (answer.getStatus() < 300) {
                user.setLanguage(language);
                try {
                    commands.updateCommands(bot, user);
                } catch (TelegramApiException | RuntimeException error) {
                    log.warn("Couldn't update commands after changing language for chat {}", user.getChatId(), error);
                }
                return supportedState();
            }
            log.warn("Couldn't change language for chat {}: HTTP {}", user.getChatId(), answer.getStatus());
        } catch (RuntimeException e) {
            log.warn("Couldn't change language for chat {}: {}", user.getChatId(), e.getMessage());
        }
        sender.sendTextMes(bot, user, texts.forUser(user, TEXT_BUNDLE).get("save-failed"));
        return supportedState();
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var text = texts.forUser(user, TEXT_BUNDLE);
        return String.format("""
                %s <i>%s</i>

                %s <b>%s</b>
                <blockquote>• %s: <b>%s</b></blockquote>
                └ <i>%s</i>
                """,
                DynamicEmoji.YOUTRADE.getEmoji(), text.get("title"),
                DynamicEmoji.SETTINGS.getEmoji(), text.get("section"),
                text.get("current"), user.effectiveLanguage().getName(), text.get("choose"));
    }
}
