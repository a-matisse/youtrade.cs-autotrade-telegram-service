package cs.youtrade.autotrade.client.telegram.menu.main.mparams.autobuy.words;

import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserWordsMenu implements MenuEnumInterface {
    WORDS_GET("📋 Посмотреть слова"),
    WORDS_ADD("➕ Добавить слова"),
    WORDS_DELETE("🗑️ Удалить слова"),
    RETURN("↩️ Назад");

    private final String buttonName;
}
