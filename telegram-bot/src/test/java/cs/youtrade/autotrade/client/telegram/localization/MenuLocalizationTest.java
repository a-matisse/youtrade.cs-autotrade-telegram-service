package cs.youtrade.autotrade.client.telegram.localization;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.notification.welcome.YTWelcomeMessageMenu;
import cs.youtrade.autotrade.client.telegram.menu.notification.welcome.YTWelcomeMessageState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.preferences.UserLanguageMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.preferences.UserLanguageState;
import cs.youtrade.autotrade.client.telegram.messaging.BotCommandProvider;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserLanguage;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralAccInfoDto;
import cs.youtrade.ytrest.RestAnswer;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.lang.reflect.Proxy;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MenuLocalizationTest {
    private final BotTexts texts = new BotTexts();
    private final BotCommandProvider commands = new BotCommandProvider(texts);
    private final UserTextMessageSender sender = new UserTextMessageSender(null);

    @Test
    void preservesRussianWelcomeLayoutExactly() {
        var state = new YTWelcomeMessageState(sender, null, texts);
        String expected = String.format("""
                %s <i>Сервис YouTrade.CS</i>

                %s <b>Добро пожаловать в Y.CS</b>
                <blockquote>Покупка и продажа предметов CS2 — автоматически.
                Подключите аккаунты и настройте торговлю под себя.</blockquote>

                %s <b>Первые шаги</b>
                <blockquote>В <a href="https://docs.youtradecs.xyz">туториале</a> — подключение аккаунтов, настройка и запуск торговли.</blockquote>

                %s <b>Language</b>
                └ <i>If needed, you can change your language below.</i>
                """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.BLINK_SMILE.getEmoji(),
                DynamicEmoji.WONDER.getEmoji(), DynamicEmoji.GLOBE.getEmoji());
        assertEquals(expected, state.getHeaderText(null, user(UserLanguage.RU), null));
    }

    @Test
    void rendersEnglishWelcomeAndPreservesNavigationAndIcons() {
        var state = new YTWelcomeMessageState(sender, null, texts);
        var user = user(UserLanguage.EN);
        var message = state.buildMessage(null, user, null);
        assertTrue(message.getText().contains("<b>Welcome to Y.CS</b>"));
        assertTrue(message.getText().contains("The <a href=\"https://docs.youtradecs.xyz\">tutorial</a> covers"));
        assertTrue(message.getText().contains(DynamicEmoji.WONDER.getEmoji()));
        assertTrue(message.getText().contains(DynamicEmoji.GLOBE.getEmoji()));
        var keyboard = ((InlineKeyboardMarkup) message.getReplyMarkup()).getKeyboard();
        assertEquals("📖 Tutorial", keyboard.getFirst().getFirst().getText());
        assertEquals("https://docs.youtradecs.xyz", keyboard.getFirst().getFirst().getUrl());
        assertEquals("NOTIFICATION_WELCOME:LANGUAGE", YTWelcomeMessageMenu.LANGUAGE.getOptionName());
        assertEquals(UserMenu.LANGUAGE, state.executeCallback(null, null, user, YTWelcomeMessageMenu.LANGUAGE));
        assertFalse(state.isReplacing());
    }

    @Test
    void rendersLanguageMenuAndBackButtonInSavedLanguage() {
        var state = new UserLanguageState(sender, null, texts, commands);
        var english = user(UserLanguage.EN);
        var russian = user(UserLanguage.RU);
        assertTrue(state.getHeaderText(null, english).contains("<b>Bot language</b>"));
        assertTrue(state.getHeaderText(null, english).contains("• Current: <b>English</b>"));
        assertTrue(state.getHeaderText(null, russian).contains("<b>Язык бота</b>"));
        assertEquals("↩️ Back", state.getTextFunctions(english).get(UserLanguageMenu.RETURN).apply(english));
        assertEquals("↩️ Назад", state.getTextFunctions(russian).get(UserLanguageMenu.RETURN).apply(russian));
        assertEquals("Русский", UserLanguageMenu.RU.getButtonName());
        assertEquals("English", UserLanguageMenu.EN.getButtonName());
    }

    @Test
    void translatesCommandDescriptionsWithoutChangingCommands() {
        var russian = commands.getBotCommands(user(UserLanguage.RU));
        var english = commands.getBotCommands(user(UserLanguage.EN));
        assertEquals(russian.stream().map(command -> command.getCommand()).toList(),
                english.stream().map(command -> command.getCommand()).toList());
        assertEquals("Главное меню", russian.getFirst().getDescription());
        assertEquals("Main menu", english.getFirst().getDescription());
        assertEquals(8, english.size());
        for (var language : UserLanguage.values()) {
            var profile = new FcdGeneralAccInfoDto();
            profile.setQualified(true);
            profile.setBargainAllowed(false);
            profile.setLanguageCode(language.getCode());
            var qualified = new UserData(3L, profile);
            var available = commands.getBotCommands(qualified);
            assertEquals(12, available.size());
            assertTrue(available.stream().allMatch(command -> !command.getDescription().isBlank()));
        }
    }

    @Test
    void preservesBlockedUserCommandRestrictions() {
        var user = user(UserLanguage.EN);
        user.setBlockedUntil("2100-01-01T00:00:00");
        var available = commands.getBotCommands(user);
        assertEquals(2, available.size());
        assertEquals("start", available.getFirst().getCommand());
        assertEquals("Dashboard and support", available.getFirst().getDescription());
        assertEquals("language", available.getLast().getCommand());
    }

    @Test
    void refreshesCommandsAndScreenImmediatelyAfterSavingLanguageWithEmptyResponse() {
        var requests = new ArrayList<SetMyCommands>();
        var bot = (TelegramClient) Proxy.newProxyInstance(TelegramClient.class.getClassLoader(),
                new Class<?>[]{TelegramClient.class}, (proxy, method, args) -> {
                    if (args[0] instanceof SetMyCommands request) requests.add(request);
                    return true;
                });
        var endpoint = new GeneralEndpoint() {
            @Override
            public RestAnswer<Void> setLanguage(Long chatId, String languageCode) {
                assertEquals("en", languageCode);
                return new RestAnswer<>(204, null);
            }
        };
        var state = new UserLanguageState(sender, endpoint, texts, commands);
        var user = user(UserLanguage.RU);
        assertEquals(UserMenu.LANGUAGE, state.executeCallback(bot, null, user, UserLanguageMenu.EN));
        assertEquals(UserLanguage.EN, user.effectiveLanguage());
        assertEquals(1, requests.size());
        assertEquals("Main menu", requests.getFirst().getCommands().getFirst().getDescription());
        assertTrue(state.getHeaderText(null, user).contains("<b>Bot language</b>"));
    }

    @Test
    void keepsSavedLanguageWhenTelegramCommandRefreshFails() {
        var bot = (TelegramClient) Proxy.newProxyInstance(TelegramClient.class.getClassLoader(),
                new Class<?>[]{TelegramClient.class}, (proxy, method, args) -> {
                    throw new IllegalStateException("Telegram unavailable");
                });
        var endpoint = new GeneralEndpoint() {
            @Override
            public RestAnswer<Void> setLanguage(Long chatId, String languageCode) {
                return new RestAnswer<>(204, null);
            }
        };
        var state = new UserLanguageState(sender, endpoint, texts, commands);
        var user = user(UserLanguage.RU);
        assertEquals(UserMenu.LANGUAGE, state.executeCallback(bot, null, user, UserLanguageMenu.EN));
        assertEquals(UserLanguage.EN, user.effectiveLanguage());
    }

    private UserData user(UserLanguage language) {
        var user = new UserData(language == UserLanguage.RU ? 1L : 2L);
        user.setLanguage(language);
        return user;
    }
}
