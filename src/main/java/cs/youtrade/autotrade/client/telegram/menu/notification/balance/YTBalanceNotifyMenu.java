package cs.youtrade.autotrade.client.telegram.menu.notification.balance;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum YTBalanceNotifyMenu implements IMenuEnum {
    PAY("💳 Пополнить", 0),
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final int rowNum;
}
