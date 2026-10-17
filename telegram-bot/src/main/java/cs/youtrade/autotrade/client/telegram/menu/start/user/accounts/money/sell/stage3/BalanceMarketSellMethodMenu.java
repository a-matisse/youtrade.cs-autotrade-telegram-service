package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage3;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum BalanceMarketSellMethodMenu implements IMenuEnum {
    USDT_TRC20("USDT TRC-20", 0),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    BalanceMarketSellMethodMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
