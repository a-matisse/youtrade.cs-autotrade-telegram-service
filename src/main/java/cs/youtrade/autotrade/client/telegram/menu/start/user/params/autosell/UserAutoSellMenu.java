package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAutoSellMenu implements IMenuEnum {
    // Изменить параметры AutoSell
    AUTOSELL_UPDATE_FIELD("⚙️ Изменить параметры", 0),
    // Изменить параметры AutoSell
    AUTOSELL_SWITCH_EVAL_MODE("🔄 EvalMode", 1),
    // Изменить параметры AutoSell
    AUTOSELL_SWITCH_EVAL_S1("🔄 EvalModeS1", 1),
    // Назад (в PARAMS)
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final int rowNum;
}
