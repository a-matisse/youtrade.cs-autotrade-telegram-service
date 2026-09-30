package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum UserPreferencesMenu implements IMenuEnum {
    BARGAIN_NOTIFICATIONS_ON("🟢 Сообщения торгов", 0),
    BARGAIN_NOTIFICATIONS_OFF("🔴 Сообщения торгов", 0),
    AUTO_RESTORE_SALES_ON("🟢 Автовосстановление продаж", 1),
    AUTO_RESTORE_SALES_OFF("🔴 Автовосстановление продаж", 1),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserPreferencesMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
