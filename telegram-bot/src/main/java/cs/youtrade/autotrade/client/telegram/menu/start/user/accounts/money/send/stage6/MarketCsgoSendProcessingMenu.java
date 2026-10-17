package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage6;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum MarketCsgoSendProcessingMenu implements IMenuEnum {
    REFRESH("🔄 Обновить", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    MarketCsgoSendProcessingMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
