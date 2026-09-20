package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.items.stage1;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum GetNewestItemsHrsMenu implements IMenuEnum {
    HOURS_1("1 ч.", 1),
    HOURS_6("6 ч.", 6),
    HOURS_12("12 ч.", 12),
    HOURS_24("24 ч.", 24);

    private final String buttonName;
    private final String optionName;
    private final int rowNum = 0;
    private final int hours;

    GetNewestItemsHrsMenu(String buttonName, int hours) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.hours = hours;
    }
}
