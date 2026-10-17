package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum UserLanguageMenu implements IMenuEnum {
    RU("Русский", 0),
    EN("English", 0),
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserLanguageMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
