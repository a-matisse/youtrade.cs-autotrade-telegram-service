package cs.youtrade.autotrade.client.telegram.menu.notification.balance;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum YTBalanceNotifyMenu implements IMenuEnum {
    PAY("💳 Пополнить", 0),
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    YTBalanceNotifyMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
