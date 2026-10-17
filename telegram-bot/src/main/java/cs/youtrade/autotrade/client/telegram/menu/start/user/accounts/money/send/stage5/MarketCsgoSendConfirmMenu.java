package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage5;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum MarketCsgoSendConfirmMenu implements IMenuEnum {
    CONFIRM("✅ Подтвердить", 0),
    CANCEL("❌ Отменить", 0);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    MarketCsgoSendConfirmMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
