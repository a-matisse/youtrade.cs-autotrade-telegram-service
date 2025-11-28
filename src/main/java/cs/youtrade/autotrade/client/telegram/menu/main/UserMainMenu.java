package cs.youtrade.autotrade.client.telegram.menu.main;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserMainMenu implements IMenuEnum {
    // Кнопка для перехода в меню параметров
    MAIN_TO_PARAMETERS("⚙️ Параметры"),
    // Вывести все параметры
    MAIN_PARAMETERS_LIST("📋 Список параметров"),
    // Переключить параметры
    MAIN_PARAMETERS_SWITCH("🔄 Сменить параметры"),
    // Создать новые параметры
    MAIN_PARAMETERS_CREATE("➕ Новые параметры"),
    // Удалить существующие параметры
    MAIN_PARAMETERS_DELETE("🗑️ Удалить параметры"),
    // Получить список новых вещей
    MAIN_GET_NEWEST_ITEMS("🆕 Новые предметы"),
    RETURN("↩️ Назад");

    private final String buttonName;
}
