package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage2;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum BalanceMarketSellAmountMenu implements IMenuEnum {
    AMOUNT_500("500 USD", 0),
    AMOUNT_1000("1000 USD", 0),
    ALL("Всё", 1),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    BalanceMarketSellAmountMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
