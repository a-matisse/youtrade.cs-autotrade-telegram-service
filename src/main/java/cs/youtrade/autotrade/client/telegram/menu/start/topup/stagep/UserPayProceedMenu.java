package cs.youtrade.autotrade.client.telegram.menu.start.topup.stagep;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserPayProceedMenu implements IMenuEnum {
    PAY("💳 Оплатить", 0),
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final int rowNum;
}
