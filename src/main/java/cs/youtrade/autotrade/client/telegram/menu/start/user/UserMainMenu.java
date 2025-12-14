package cs.youtrade.autotrade.client.telegram.menu.start.user;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserMainMenu implements IMenuEnum {
    // Кнопка для перехода в меню параметров
    MAIN_TO_PARAMETERS("⚙️ К текущим параметрам", 0),
    // Вывести все параметры
    MAIN_PARAMETERS_LIST("📋 Список всех параметров", 1),
    // Переключить параметры
    MAIN_PARAMETERS_SWITCH("🔄 Сменить", 2),
    // Создать новые параметры
    MAIN_PARAMETERS_CREATE("➕ Новые", 2),
    // Удалить существующие параметры
    MAIN_PARAMETERS_DELETE("🗑️ Удалить", 2),
    // Получить список новых вещей
    MAIN_GET_NEWEST_ITEMS("🌐 Общая история лотов", 3),
    RETURN("↩️ Назад", 4);

    private final String buttonName;
    private final int rowNum;
}
