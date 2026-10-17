package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage4;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum MarketCsgoSendAmountMenu implements IMenuEnum {
    AMOUNT_200("200 USD", 0),
    AMOUNT_500("500 USD", 0),
    AMOUNT_1000("1000 USD", 1),
    ALL("Всё", 1),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    MarketCsgoSendAmountMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
