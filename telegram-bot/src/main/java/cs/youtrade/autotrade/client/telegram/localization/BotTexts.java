package cs.youtrade.autotrade.client.telegram.localization;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserLanguage;
import cs.youtrade.telegram.buttons.IMenuEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BotTexts {
    private static final ResourceBundle.Control CONTROL = ResourceBundle.Control
            .getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    public LocalizedTexts forUser(UserData user, String bundle) {
        return forLanguage(user.effectiveLanguage(), bundle);
    }

    public <M extends IMenuEnum> Map<M, Function<UserData, String>> buttons(M[] options) {
        return Arrays.stream(options).collect(Collectors.toMap(Function.identity(), option -> user -> button(user, option)));
    }

    private String button(UserData user, IMenuEnum option) {
        String original = option.getButtonName();
        if (!(option instanceof Enum<?> value))
            return original;
        String bundle = MenuTextBundles.BUNDLES.get(value.getDeclaringClass().getSimpleName());
        if (bundle == null)
            return original;
        String key = "button." + value.name();
        var russian = forLanguage(UserLanguage.RU, bundle);
        if (!russian.contains(key))
            return original;
        return original.replace(russian.get(key), forUser(user, bundle).get(key));
    }

    public LocalizedTexts forLanguage(UserLanguage language, String bundle) {
        Locale locale = Locale.forLanguageTag(language.getCode());
        String basename = "i18n." + bundle;
        ResourceBundle english = ResourceBundle.getBundle(basename, Locale.ENGLISH, CONTROL);
        ResourceBundle translated;
        try {
            translated = ResourceBundle.getBundle(basename, locale, CONTROL);
        } catch (MissingResourceException error) {
            return new LocalizedTexts(this, language, Locale.ENGLISH, english, english);
        }
        return new LocalizedTexts(this, language, locale, translated, english);
    }
}
