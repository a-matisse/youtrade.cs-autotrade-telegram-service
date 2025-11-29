package cs.youtrade.autotrade.client.telegram.menu.main.params;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserParamsMenu implements IMenuEnum {
    // Переименовать параметры
    PARAMS_RENAME("✏️ Переименовать", 0),
    // К настройкам автопокупки
    PARAMS_TO_AUTOBUY("📥 AutoBuy", 1),
    // К настройкам автопродажи
    PARAMS_TO_AUTOSELL("📤 AutoSell", 1),
    // К настройкам следования
    PARAMS_TO_FOLLOW("👥 Follow", 1),
    // К настройкам токенов
    PARAMS_TO_TOKENS("🔑 Токены", 2),
    // Назад (в MAIN)
    RETURN("↩️ Назад", 3);

    private final String buttonName;
    private final int rowNum;
}
