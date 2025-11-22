package cs.youtrade.autotrade.client.telegram.menu.main.mparams;

import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserParamsMenu implements MenuEnumInterface {
    // Переименовать параметры
    PARAMS_RENAME("✏️ Переименовать"),
    // К настройкам автопокупки
    PARAMS_TO_AUTOBUY("🤖 Настройка AutoBuy"),
    // К настройкам автопродажи
    PARAMS_TO_AUTOSELL("💰 Настройка AutoSell"),
    // К настройкам следования
    PARAMS_TO_FOLLOW("👀 Настройка Follow"),
    // К настройкам токенов
    PARAMS_TO_TOKENS("🎫 Меню Токенов"),
    // Назад (в MAIN)
    RETURN("↩️ Назад");

    private final String buttonName;
}
