package cs.youtrade.autotrade.client.telegram.menu.start.user.params;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserDeepParamsMenu implements IMenuEnum {
    // К настройкам автопокупки
    PARAMS_TO_AUTOBUY("📥 AutoBuy", 0),
    // К настройкам автопродажи
    PARAMS_TO_AUTOSELL("📤 AutoSell", 0),
    // Переименовать параметры
    PARAMS_RENAME("✏️ Переименовать", 1),
    // Вывести все параметры
    PARAMS_LIST("📋 Список всех параметров", 2),
    // Переключить параметры
    PARAMS_SWITCH("🔄 Сменить", 3),
    // Создать новые параметры
    PARAMS_CREATE("➕ Новые", 3),
    // Удалить существующие параметры
    PARAMS_DELETE("🗑️ Удалить", 3),
    // Назад (в MAIN)
    RETURN("↩️ Назад", 4);

    private final String buttonName;
    private final int rowNum;
}
