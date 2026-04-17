package cs.youtrade.autotrade.client.telegram.menu.start.user;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserParamsMenu implements IMenuEnum {
    // Быстрая настройка
    PARAMS_QUICK_ENABLE("🔋 Быстрая настройка", 0),
    PARAMS_QUICK_DISABLE("🪫 Быстрая настройка", 0),
    // Остальные настройки
    PARAMS_TO_TOKENS("🔑 Аккаунты", 1),
    PARAMS_PORTFOLIO("💼 Портфель", 1),
    PARAMS_SWITCH("🔄 Сменить", 1),
    // Покупка
    PARAMS_BUY_ON("🟢 Покупка", 2),
    PARAMS_BUY_OFF("🔴 Покупка", 2),
    // Продажа
    PARAMS_SELL_ON("🟢 Продажа", 2),
    PARAMS_SELL_OFF("🔴 Продажа", 2),
    // Назад
    RETURN("↩️ Назад", 3),
    TO_DEEP_PARAMS("▶️ Углублённая", 3);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserParamsMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
