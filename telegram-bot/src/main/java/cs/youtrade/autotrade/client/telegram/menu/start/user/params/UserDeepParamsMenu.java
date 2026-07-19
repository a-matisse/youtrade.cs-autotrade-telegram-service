package cs.youtrade.autotrade.client.telegram.menu.start.user.params;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserDeepParamsMenu implements IMenuEnum {
    // Переключение режима торговли
    MARKET_MODE_MARKET("🧊 Режим: Рыночный", 0),
    MARKET_MODE_BARGAIN("🔥 Режим: Баргейны", 0),
    // К настройкам автопокупки
    PARAMS_TO_AUTOBUY("📥 Автопокупка", 1),
    // К настройкам автопродажи
    PARAMS_TO_AUTOSELL("📤 Автопродажа", 1),
    // Переименовать параметры
    PARAMS_RENAME("✏️ Переименовать", 2),
    // Включить следование за параметрами
    PARAMS_TO_FOLLOW("👥 Следование", 2),
    // Создать новые параметры
    PARAMS_CREATE("➕ Новые", 3),
    // Вывести все параметры
    PARAMS_LIST("📋 Список всех", 3),
    // Удалить существующие параметры
    PARAMS_DELETE("🗑️ Удалить", 3),
    // Назад (в MAIN)
    RETURN("↩️ Назад", 4),
    TO_QUICK_CONFIG("◀️ Быстрая", 4);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserDeepParamsMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
