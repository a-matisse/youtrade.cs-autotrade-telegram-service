package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserWordsMenu implements IMenuEnum {
    WORDS_GET("📋 Посмотреть", 0),
    WORDS_ADD("➕ Добавить", 0),
    WORDS_DELETE("🗑️ Удалить", 0),
    WORDS_DELETE_ALL("💥 Удалить ВСЕ", 1),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final int rowNum;
}
