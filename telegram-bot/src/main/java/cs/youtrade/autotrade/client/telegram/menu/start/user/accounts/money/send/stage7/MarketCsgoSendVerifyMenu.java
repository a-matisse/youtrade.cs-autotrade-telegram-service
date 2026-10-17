package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage7;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum MarketCsgoSendVerifyMenu implements IMenuEnum {
    RECEIVED("✅ Деньги пришли", 0),
    NOT_RECEIVED("❌ Не пришли", 0),
    REFRESH("🔄 Обновить", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    MarketCsgoSendVerifyMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
