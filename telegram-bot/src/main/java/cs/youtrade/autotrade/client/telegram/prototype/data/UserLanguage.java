package cs.youtrade.autotrade.client.telegram.prototype.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum UserLanguage {
    RU("ru", "Русский"),
    EN("en", "English");

    private final String code;
    private final String name;
    private static final Map<String, UserLanguage> BY_CODE = Arrays
            .stream(UserLanguage.values())
            .collect(Collectors.toMap(UserLanguage::getCode, Function.identity()));

    public static UserLanguage fromTelegram(String languageCode) {
        if (languageCode == null || languageCode.isBlank()) return EN;
        return BY_CODE.getOrDefault(Locale.forLanguageTag(languageCode).getLanguage(), EN);
    }

    public static UserLanguage fromStored(String languageCode) {
        return languageCode == null ? null : BY_CODE.get(languageCode);
    }
}
