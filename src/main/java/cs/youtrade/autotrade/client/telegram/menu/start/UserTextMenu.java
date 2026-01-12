package cs.youtrade.autotrade.client.telegram.menu.start;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTextMenu implements IMenuEnum {
    USER("📊 Мой кабинет", 0),
    REF("💎 Реферальная программа", 1),
    TOP_UP("💳 Пополнить", 2),
    GET_PRICE("💰 Узнать цены", 2),
    GROUP_URL("📢 Группа", 3),
    SUPPORT_URL("🆘 Поддержка", 3);

    private final String buttonName;
    private final int rowNum;
}
