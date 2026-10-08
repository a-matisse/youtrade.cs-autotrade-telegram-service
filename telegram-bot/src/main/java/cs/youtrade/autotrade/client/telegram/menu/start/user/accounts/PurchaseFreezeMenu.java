package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum PurchaseFreezeMenu implements IMenuEnum {
    ONE_DAY("1 день", 0),
    SEVEN_DAYS("7 дней", 0),
    THIRTY_DAYS("30 дней", 1),
    RESUME("🟢 Возобновить", 1),
    BACK("↩️ Назад", 2);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    PurchaseFreezeMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
