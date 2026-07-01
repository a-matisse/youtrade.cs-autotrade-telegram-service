package cs.youtrade.autotrade.client.telegram.menu.start.user.params;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserDeepParamsMenu implements IMenuEnum {
    // К настройкам автопокупки
    PARAMS_TO_AUTOBUY("📥 Автопокупка", 0),
    // К настройкам автопродажи
    PARAMS_TO_AUTOSELL("📤 Автопродажа", 0),
    // Переименовать параметры
    PARAMS_RENAME("✏️ Переименовать", 1),
    // Включить следование за параметрами
    PARAMS_TO_FOLLOW("👥 Следование", 1),
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
