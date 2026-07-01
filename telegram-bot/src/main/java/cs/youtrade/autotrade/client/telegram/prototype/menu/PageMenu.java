package cs.youtrade.autotrade.client.telegram.prototype.menu;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum PageMenu implements IMenuEnum {
    PREVIOUS("◀️ Назад", 0),
    NEXT("▶️ Вперед", 0),
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    PageMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
