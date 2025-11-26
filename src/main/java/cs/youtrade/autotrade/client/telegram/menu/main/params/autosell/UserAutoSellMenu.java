package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAutoSellMenu implements IMenuEnum {
    // Изменить параметры AutoSell
    AUTOSELL_UPDATE_FIELD("⚙️ Изменить параметры AutoSell"),
    // Изменить параметры AutoSell
    AUTOSELL_SWITCH_EVAL_MODE("🔄 Сменить режим evalMode"),
    // Изменить параметры AutoSell
    AUTOSELL_SWITCH_EVAL_S1("🔄 Сменить режим evalModeS1"),
    // Вкл/Выкл автопокупку
    AUTOSELL_TOGGLE_AUTOSELL("🚀 Вкл/Выкл AutoSell"),
    // Меню таблиц данных
    AUTOSELL_TO_TABLES("📋 Меню таблиц данных"),
    // Назад (в PARAMS)
    RETURN("↩️ Назад");

    private final String buttonName;
}
