package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words;

import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserWordsMenu implements MenuEnumInterface {
    WORDS_GET("📋 Посмотреть слова"),
    WORDS_ADD("➕ Добавить слова"),
    WORDS_DELETE("🗑️ Удалить слова"),
    WORDS_DELETE_ALL("💥 Удалить ВСЕ слова"),
    RETURN("↩️ Назад");

    private final String buttonName;
}
