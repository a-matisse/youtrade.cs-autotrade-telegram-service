package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table;

import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTableMenu implements MenuEnumInterface {
    TABLE_SELLING("🛒 Предметы в продаже"),
    TABLE_WAITING("⏰ Предметы в ожидании"),
    TABLE_HISTORY("📊 История продаж"),
    TABLE_UPLOAD("📤 Выставить на продажу"),
    TABLE_CHANGE("✏️ Изменить позиции (Одиночное)"),
    TABLE_GROUPS_CHANGE("✏️ Изменить позиции (Групповое)"),
    TABLE_RESTRICT("🚫 Запретить к продаже"),
    // Назад (в AUTOSELL)
    RETURN("↩️ Назад");

    private final String buttonName;
}
