package cs.youtrade.autotrade.client.telegram.localization;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.preferences.*;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserLanguage;
import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenu;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.ytrest.RestAnswer;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesLocalizationTest {
    private final BotTexts texts = new BotTexts();
    private final UserTextMessageSender sender = new UserTextMessageSender(null);

    @Test
    void preservesRussianSettingsAndProfileLayoutExactly() {
        var user = user(UserLanguage.RU);
        var state = preferences();
        String expected = String.format("""
                %s <i>Настройки</i>

                %s <b>Профиль</b>
                <blockquote>• ID пользователя: <b><code>123</code></b>
                • Текущий params-ID: <b><code>456</code></b>
                • Имя маршрута: <b>Test route</b></blockquote>

                %s <b>Личные настройки</b>
                <blockquote>%s Сообщения торгов: <b>Полные</b></blockquote>
                <blockquote>%s Автовосстановление продаж: <b>Выключено</b></blockquote>

                %s <b>Данные аккаунтов</b>
                <blockquote>• Защитный пароль: <b>Не установлен</b></blockquote>
                """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.PROFILE.getEmoji(),
                DynamicEmoji.SETTINGS.getEmoji(), DynamicEmoji.ON.getEmoji(),
                DynamicEmoji.OFF.getEmoji(), DynamicEmoji.SECURE.getEmoji());
        assertEquals(expected, state.getHeaderText(null, user));
    }

    @Test
    void translatesSettingsAndButtonsWithoutChangingCallbacksOrVisibility() {
        var state = preferences();
        var user = user(UserLanguage.EN);
        String header = state.getHeaderText(null, user);
        assertTrue(header.contains("<i>Settings</i>"));
        assertTrue(header.contains("• User ID: <b><code>123</code></b>"));
        assertTrue(header.contains("Bargain messages: <b>Full</b>"));
        assertTrue(header.contains("Security password: <b>Not set</b>"));
        assertFalse(header.matches("(?s).*[А-Яа-яЁё].*"));
        assertEquals("🔑 Set password", state.getTextFunctions(user)
                .get(UserPreferencesMenu.SET_ACCOUNT_PASSWORD).apply(user));
        assertTrue(state.getVisibilityPredicates(user).get(UserPreferencesMenu.SET_ACCOUNT_PASSWORD).test(user));
        assertFalse(state.getVisibilityPredicates(user).get(UserPreferencesMenu.CHANGE_ACCOUNT_PASSWORD).test(user));
        user.setAccountExportPasswordSet(true);
        assertTrue(state.getVisibilityPredicates(user).get(UserPreferencesMenu.CHANGE_ACCOUNT_PASSWORD).test(user));
        assertEquals("CHANGE_ACCOUNT_PASSWORD", UserPreferencesMenu.CHANGE_ACCOUNT_PASSWORD.getOptionName());
        assertEquals(UserMenu.PREFERENCES, state.supportedState());
    }

    @Test
    void preservesRussianPasswordFormAndUsesWholeSuccessPhrases() {
        var registry = new AccountPasswordRegistry();
        var state = new AccountPasswordResultState(sender, registry, texts);
        var user = user(UserLanguage.RU);
        var data = registry.getOrCreate(user, AccountPasswordData::new);
        data.setResponseStatus(204);
        data.setSaved(true);
        String expected = String.format("""
                %s <i>Защитный пароль</i>

                %s <b>Данные аккаунтов</b>
                <blockquote>• Действие: <b>Установка пароля</b>
                • Новый пароль: ✅ Сохранён</blockquote>
                └ <i>Один пароль для всех аккаунтов. От 12 до 256 символов.</i>

                %s <b>Пароль установлен</b>

                """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.SECURE.getEmoji(), DynamicEmoji.SUCCESS.getEmoji());
        assertEquals(expected, state.getHeaderText(null, user));
        user.setLanguage(UserLanguage.EN);
        assertTrue(state.getHeaderText(null, user).contains("<b>Password set</b>"));
        data.setChanging(true);
        data.setCurrentProvided(true);
        assertTrue(state.getHeaderText(null, user).contains("• Current password: ✅ Received"));
        assertTrue(state.getHeaderText(null, user).contains("<b>Password changed</b>"));
        assertEquals("↩️ Back", state.getTextFunctions(user).get(TerminalMenu.RETURN).apply(user));
    }

    @Test
    void translatesEveryPasswordResultAndBothInputStages() {
        var registry = new AccountPasswordRegistry();
        var result = new AccountPasswordResultState(sender, registry, texts);
        var user = user(UserLanguage.EN);
        var data = registry.getOrCreate(user, AccountPasswordData::new);
        for (int status : new int[]{204, 403, 409, 429, 0}) {
            data.setResponseStatus(status);
            assertFalse(result.getHeaderText(null, user).matches("(?s).*[А-Яа-яЁё].*"), "HTTP " + status);
        }
        var current = new AccountPasswordState(sender, registry, texts);
        var next = new AccountPasswordNewState(sender, registry, null, texts);
        assertTrue(current.getHeaderText(null, user).contains("<b>Send your current password</b>"));
        assertTrue(next.getHeaderText(null, user).contains("<b>Send your new password</b>"));
    }

    @Test
    void keepsConnectedDictionariesFreeOfLayoutAndEmoji() {
        for (String bundle : new String[]{"preferences.UserPreferences", "preferences.AccountPassword", "start.UserStart"}) {
            var russian = ResourceBundle.getBundle("i18n." + bundle, Locale.forLanguageTag("ru"));
            var english = ResourceBundle.getBundle("i18n." + bundle, Locale.ENGLISH);
            assertEquals(russian.keySet(), english.keySet());
            for (var resource : new ResourceBundle[]{russian, english}) {
                for (String key : resource.keySet()) {
                    String value = resource.getString(key);
                    assertFalse(value.contains("<"), bundle + ": " + key);
                    assertFalse(value.contains("\n"), bundle + ": " + key);
                    assertFalse(value.codePoints().anyMatch(c -> c >= 0x1F000), bundle + ": " + key);
                }
            }
        }
    }

    private UserPreferencesState preferences() {
        var params = new FcdParamsGetDto();
        params.setTdId(123L);
        params.setTdpId(456L);
        params.setGivenName("Test route");
        params.setSource(MarketType.CSFLOAT);
        params.setBargainNotifications(true);
        params.setAutoRestoreSales(false);
        var endpoint = new ParamsEndpoint() {
            @Override
            public RestAnswer<FcdDefaultDto<FcdParamsGetDto>> getCurrent(Long chatId) {
                return new RestAnswer<>(200, new FcdDefaultDto<>(params));
            }
        };
        return new UserPreferencesState(sender, endpoint, null, new AccountPasswordRegistry(), texts);
    }

    private UserData user(UserLanguage language) {
        var user = new UserData(1L);
        user.setLanguage(language);
        return user;
    }
}
