package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum UserPreferencesMenu implements IMenuEnum {
    BARGAIN_NOTIFICATIONS_ON("🟢", "Сообщения торгов", 0),
    BARGAIN_NOTIFICATIONS_OFF("🔴", "Сообщения торгов", 0),
    AUTO_RESTORE_SALES_ON("🟢", "Автовосстановление продаж", 1),
    AUTO_RESTORE_SALES_OFF("🔴", "Автовосстановление продаж", 1),
    SET_ACCOUNT_PASSWORD("🔑", "Установить пароль", 2),
    CHANGE_ACCOUNT_PASSWORD("🔑", "Сменить пароль", 2),
    RETURN("↩️", "Назад", 3);

    private final String emoji;
    private final String label;
    private final String optionName;
    private final int rowNum;

    UserPreferencesMenu(String emoji, String label, int rowNum) {
        this.emoji = emoji;
        this.label = label;
        this.optionName = name();
        this.rowNum = rowNum;
    }

    @Override
    public String getButtonName() {
        return getButtonName(label);
    }

    public String getButtonName(String text) {
        return emoji + " " + text;
    }
}
