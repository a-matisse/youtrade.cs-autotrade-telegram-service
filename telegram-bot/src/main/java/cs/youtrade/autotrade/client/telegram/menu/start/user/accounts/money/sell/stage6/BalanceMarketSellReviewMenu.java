package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage6;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum BalanceMarketSellReviewMenu implements IMenuEnum {
    CONFIRM("✅ Подтвердить", 0),
    ADDRESS_CHANGE("✏️ Изменить", 0),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    BalanceMarketSellReviewMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
