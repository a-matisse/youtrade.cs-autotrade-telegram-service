package cs.youtrade.autotrade.client.telegram.localization;

import com.ibm.icu.text.MessageFormat;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserLanguage;
import cs.youtrade.telegram.buttons.IMenuEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class LocalizationCoverageTest {
    private final BotTexts texts = new BotTexts();

    @Test
    void validatesAllDictionariesAndFormatsEveryPhraseInBothLanguages() throws Exception {
        var checks = new ArrayList<Executable>();
        Path root = Path.of("src/main/resources/i18n");
        try (var paths = Files.walk(root)) {
            for (var path : paths.filter(p -> p.toString().endsWith("_ru.properties")).toList()) {
                String bundle = root.relativize(path).toString().replace('\\', '.')
                        .replace('/', '.').replace("_ru.properties", "");
                var ru = ResourceBundle.getBundle("i18n." + bundle, Locale.forLanguageTag("ru"));
                var en = ResourceBundle.getBundle("i18n." + bundle, Locale.ENGLISH);
                assertFalse(ru.keySet().isEmpty(), bundle);
                assertEquals(ru.keySet(), en.keySet(), bundle);
                for (String key : ru.keySet()) {
                    checks.add(() -> {
                    var ruFormat = new MessageFormat(ru.getString(key), Locale.forLanguageTag("ru"));
                    var enFormat = new MessageFormat(en.getString(key), Locale.ENGLISH);
                    assertEquals(ruFormat.getArgumentNames(), enFormat.getArgumentNames(), bundle + ": " + key);
                    var values = new HashMap<String, Object>();
                    ruFormat.getArgumentNames().forEach(name -> values.put(name, 2));
                    for (var language : UserLanguage.values()) {
                        var pattern = language == UserLanguage.RU ? ru.getString(key) : en.getString(key);
                        assertFalse(pattern.matches("(?s).*<[/]?[A-Za-z][^>]*>.*"), bundle + ": " + key);
                        assertFalse(pattern.contains("\\u000"), bundle + ": " + key);
                        assertFalse(pattern.codePoints().anyMatch(c -> c < 32), bundle + ": " + key);
                        assertFalse(pattern.contains("→"), bundle + ": " + key);
                        assertFalse(pattern.codePoints().anyMatch(c -> c >= 0x1F000 || c >= 0x2600 && c <= 0x27FF), bundle + ": " + key);
                        texts.forLanguage(language, bundle).format(key, values);
                    }
                    });
                }
            }
        }
        assertAll(checks);
    }

    @Test
    void translatesEveryMenuEnumAndPreservesRussianCaptionsAndIcons() throws Exception {
        var russian = new UserData(1L);
        russian.setLanguage(UserLanguage.RU);
        var english = new UserData(2L);
        english.setLanguage(UserLanguage.EN);
        Path root = Path.of("target/classes");
        try (var paths = Files.walk(root.resolve("cs/youtrade/autotrade/client"))) {
            for (var path : paths.filter(p -> p.toString().endsWith(".class")).toList()) {
                String name = root.relativize(path).toString().replace('\\', '.')
                        .replace('/', '.').replace(".class", "");
                Class<?> type = Class.forName(name, false, getClass().getClassLoader());
                if (!type.isEnum() || !IMenuEnum.class.isAssignableFrom(type)) continue;
                for (Object constant : type.getEnumConstants()) {
                    var option = (IMenuEnum) constant;
                    var functions = texts.buttons(new IMenuEnum[]{option});
                    String original = option.getButtonName();
                    if (type.getSimpleName().equals("UserLanguageMenu")) continue;
                    assertEquals(original, functions.get(option).apply(russian), name + ": " + constant);
                    String translated = functions.get(option).apply(english);
                    assertFalse(translated.matches("(?s).*[А-Яа-яЁё].*"), name + ": " + constant + " = " + translated);
                    if (original.codePoints().anyMatch(c -> c >= 0x1F000))
                        assertEquals(original.codePointAt(0), translated.codePointAt(0), name + ": " + constant);
                }
            }
        }
    }

    @Test
    void keepsLanguageWhenChangingBundlesAfterEnglishFallback() {
        var fallback = texts.forLanguage(UserLanguage.RU, "test.EnglishOnly");
        var switched = fallback.inBundle("preferences.UserPreferences");
        assertEquals("Настройки", switched.get("title"));
        assertEquals("Settings", switched.inLanguage(UserLanguage.EN).get("title"));
    }
}
