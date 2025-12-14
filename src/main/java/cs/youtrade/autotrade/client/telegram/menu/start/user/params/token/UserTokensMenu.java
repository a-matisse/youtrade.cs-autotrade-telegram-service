package cs.youtrade.autotrade.client.telegram.menu.start.user.params.token;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTokensMenu implements IMenuEnum {
    TOKEN_GET("👀 Просмотреть все", 0),
    TOKEN_ADD("➕ Добавить", 1),
    TOKEN_REMOVE("🗑️ Удалить", 1),
    TOKEN_RENAME("✏️ Переименовать", 1),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final int rowNum;
}
