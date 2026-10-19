package cs.youtrade.autotrade.client.telegram.localization;

import com.ibm.icu.text.MessageFormat;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserLanguage;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class LocalizedTexts {
    private final BotTexts texts;
    private final UserLanguage language;
    private final Locale locale;
    private final ResourceBundle translated;
    private final ResourceBundle english;

    public LocalizedTexts inBundle(String bundle) {
        return texts.forLanguage(language, bundle);
    }

    public LocalizedTexts inLanguage(UserLanguage selected) {
        return texts.forLanguage(selected, english.getBaseBundleName().substring("i18n.".length()));
    }

    public String get(String key) {
        return format(key, Map.of());
    }

    public boolean contains(String key) {
        return translated.containsKey(key) || english.containsKey(key);
    }

    public String enumText(Enum<?> value, String field) {
        return get(value.getDeclaringClass().getSimpleName() + "." + value.name() + "." + field);
    }

    public String format(String key, Map<String, ?> arguments) {
        boolean localized = translated.containsKey(key);
        String pattern = localized
                ? translated.getString(key) : english.getString(key);
        var formatter = new MessageFormat(pattern, localized ? locale : Locale.ENGLISH);
        if (!arguments.keySet().containsAll(formatter.getArgumentNames()))
            throw new IllegalArgumentException("Missing arguments for localized text: " + key);
        return formatter.format(arguments);
    }
}
