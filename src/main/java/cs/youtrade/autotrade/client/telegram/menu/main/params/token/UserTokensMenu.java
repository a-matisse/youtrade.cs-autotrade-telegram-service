package cs.youtrade.autotrade.client.telegram.menu.main.params.token;

import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTokensMenu implements MenuEnumInterface {
    TOKEN_GET("👀 Просмотреть токены"),
    TOKEN_BUY_ADD("➕ Добавить токен покупки"),
    TOKEN_SELL_ADD("💰 Добавить токен продажи"),
    TOKEN_DELETE("🗑️ Удалить токен"),
    RETURN("↩️ Назад");

    private final String buttonName;
}
