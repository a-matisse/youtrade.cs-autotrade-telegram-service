package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stagep;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum BalanceMarketSellResultMenu implements IMenuEnum {
    REFRESH("🔄 Обновить", 0),
    CANCEL_SESSION("❌ Отменить", 0),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    BalanceMarketSellResultMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
