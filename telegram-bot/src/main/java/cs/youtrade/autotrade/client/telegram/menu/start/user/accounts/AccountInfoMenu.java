package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum AccountInfoMenu implements IMenuEnum {
    BACK("↩️ Назад", 0);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    AccountInfoMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
