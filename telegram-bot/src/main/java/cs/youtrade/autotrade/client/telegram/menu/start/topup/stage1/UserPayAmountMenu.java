package cs.youtrade.autotrade.client.telegram.menu.start.topup.stage1;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum UserPayAmountMenu implements IMenuEnum {
    USD_25("$25", 25),
    USD_100("$100", 100),
    USD_250("$250", 250),
    USD_1000("$1000", 1000);

    private final String buttonName;
    private final String optionName;
    private final int rowNum = 0;
    private final double amount;

    UserPayAmountMenu(String buttonName, double amount) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.amount = amount;
    }
}
