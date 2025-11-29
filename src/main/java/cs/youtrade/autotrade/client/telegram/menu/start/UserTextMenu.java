package cs.youtrade.autotrade.client.telegram.menu.start;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTextMenu implements IMenuEnum {
    MAIN("📊 Мой кабинет", 0),
    TOP_UP("💳 Пополнить баланс", 1),
    GET_PRICE("💰 Узнать цены", 2);

    private final String buttonName;
    private final int rowNum;
}
