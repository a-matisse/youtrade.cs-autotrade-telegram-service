package cs.youtrade.autotrade.client.telegram.menu.main;

import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserMainMenu implements MenuEnumInterface {
    // Вывести информацию об аккаунте
    MAIN_VIEW_ACC_INFO("👤 Аккаунт"),
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
    MAIN_GET_NEWEST_ITEMS("🆕 Новые предметы");

    private final String buttonName;
}
