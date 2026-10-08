package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum AccountPasswordMenu implements IMenuEnum {
    BACK("↩️ Назад", 0);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    AccountPasswordMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
