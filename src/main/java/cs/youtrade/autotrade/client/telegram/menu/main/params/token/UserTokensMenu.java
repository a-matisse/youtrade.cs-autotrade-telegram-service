package cs.youtrade.autotrade.client.telegram.menu.main.params.token;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTokensMenu implements IMenuEnum {
    TOKEN_GET("👀 Просмотреть токены"),
    TOKEN_ADD("➕ Добавить токен"),
    TOKEN_REMOVE("🗑️ Удалить токен"),
    TOKEN_RENAME("✏️ Переименовать токен"),
    RETURN("↩️ Назад");

    private final String buttonName;
}
