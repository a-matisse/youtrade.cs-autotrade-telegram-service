package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage7;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum BalanceMarketSellProcessingMenu implements IMenuEnum {
    REFRESH("🔄 Обновить", 0);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    BalanceMarketSellProcessingMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
