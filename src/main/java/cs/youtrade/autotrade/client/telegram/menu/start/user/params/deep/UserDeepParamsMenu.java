package cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserDeepParamsMenu implements IMenuEnum {
    // К настройкам автопокупки
    DEEP_PARAMS_TO_AUTOBUY("📥 AutoBuy", 0),
    // К настройкам автопродажи
    DEEP_PARAMS_TO_AUTOSELL("📤 AutoSell", 0),
    // Переименовать параметры
    DEEP_PARAMS_RENAME("✏️ Переименовать", 1),
    // Назад (в MAIN)
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final int rowNum;
}
