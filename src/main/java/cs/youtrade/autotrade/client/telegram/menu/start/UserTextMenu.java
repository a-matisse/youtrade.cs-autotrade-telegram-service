package cs.youtrade.autotrade.client.telegram.menu.start;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTextMenu implements IMenuEnum {
    MAIN("📊 Главное меню"),
    TOP_UP("💳 Пополнить баланс"),
    GET_PRICE("💰 Узнать цены");

    private final String buttonName;
}
