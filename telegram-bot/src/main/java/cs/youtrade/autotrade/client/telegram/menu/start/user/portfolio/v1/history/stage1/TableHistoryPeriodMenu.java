package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stage1;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum TableHistoryPeriodMenu implements IMenuEnum {
    DAYS_3("3 дн.", 3),
    DAYS_7("7 дн.", 7),
    DAYS_14("14 дн.", 14),
    DAYS_30("30 дн.", 30);

    private final String buttonName;
    private final String optionName;
    private final int rowNum = 0;
    private final int days;

    TableHistoryPeriodMenu(String buttonName, int days) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.days = days;
    }
}
