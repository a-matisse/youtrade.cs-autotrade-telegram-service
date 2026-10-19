package cs.youtrade.autotrade.client.telegram.localization;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserLanguage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class BotTextsTest {
    private final BotTexts texts = new BotTexts();

    @Test
    void selectsSavedLanguageAndSubstitutesNamedArguments() {
        assertEquals("Привет, Alice!", texts.forUser(user(UserLanguage.RU), "test.Messages")
                .format("greeting", Map.of("name", "Alice")));
        assertEquals("Hello, Alice!", texts.forUser(user(UserLanguage.EN), "test.Messages")
                .format("greeting", Map.of("name", "Alice")));
    }

    @Test
    void defaultsToEnglishIndependentlyOfServerLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("ru"));
            var user = new UserData(1L);
            assertEquals("Hello, Alice!", texts.forUser(user, "test.Messages")
                    .format("greeting", Map.of("name", "Alice")));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void usesRussianPluralRulesIncludingTeensAndCompoundNumbers() {
        var text = texts.forLanguage(UserLanguage.RU, "test.Messages");
        var expected = Map.of(0, "0 дней", 1, "1 день", 2, "2 дня", 5, "5 дней",
                11, "11 дней", 21, "21 день", 22, "22 дня", 25, "25 дней", 111, "111 дней");
        expected.forEach((days, value) -> assertEquals(value, text.format("days", Map.of("days", days))));
    }

    @Test
    void usesEnglishPluralRules() {
        var text = texts.forLanguage(UserLanguage.EN, "test.Messages");
        assertEquals("1 day", text.format("days", Map.of("days", 1)));
        assertEquals("2 days", text.format("days", Map.of("days", 2)));
        assertEquals("21 days", text.format("days", Map.of("days", 21)));
    }

    @Test
    void fallsBackPerKeyUsingEnglishPluralRules() {
        var text = texts.forLanguage(UserLanguage.RU, "test.Messages");
        assertEquals("21 days", text.format("fallback-days", Map.of("days", 21)));
        assertEquals("Friend's account", text.get("apostrophe"));
    }

    @Test
    void fallsBackForAnEntireMissingLanguageBundle() {
        assertEquals("21 days", texts.forLanguage(UserLanguage.RU, "test.EnglishOnly")
                .format("days", Map.of("days", 21)));
    }

    @Test
    void rejectsMissingKeysBundlesAndArguments() {
        var text = texts.forLanguage(UserLanguage.EN, "test.Messages");
        assertThrows(MissingResourceException.class, () -> text.get("unknown"));
        assertThrows(MissingResourceException.class, () -> texts.forLanguage(UserLanguage.EN, "test.Unknown"));
        assertThrows(IllegalArgumentException.class, () -> text.get("greeting"));
    }

    @Test
    void keepsArgumentTextLiteral() {
        assertEquals("Hello, O'Brien {days}!", texts.forLanguage(UserLanguage.EN, "test.Messages")
                .format("greeting", Map.of("name", "O'Brien {days}")));
    }

    @Test
    void doesNotMixLanguagesBetweenConcurrentUsers() throws Exception {
        var russian = user(UserLanguage.RU);
        var english = user(UserLanguage.EN);
        var calls = new ArrayList<Callable<Void>>();
        for (int i = 0; i < 100; i++) {
            calls.add(() -> {
                assertEquals("22 дня", texts.forUser(russian, "test.Messages")
                        .format("days", Map.of("days", 22)));
                assertEquals("22 days", texts.forUser(english, "test.Messages")
                        .format("days", Map.of("days", 22)));
                return null;
            });
        }
        try (var executor = Executors.newFixedThreadPool(8)) {
            for (var result : executor.invokeAll(calls)) result.get();
        }
    }

    private UserData user(UserLanguage language) {
        var user = new UserData(language == UserLanguage.RU ? 1L : 2L);
        user.setLanguage(language);
        return user;
    }
}
