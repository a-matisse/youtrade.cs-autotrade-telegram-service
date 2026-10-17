package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.stage3;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum MarketCsgoCollectProcessingMenu implements IMenuEnum {
    REFRESH("🔄 Обновить", 0);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    MarketCsgoCollectProcessingMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
