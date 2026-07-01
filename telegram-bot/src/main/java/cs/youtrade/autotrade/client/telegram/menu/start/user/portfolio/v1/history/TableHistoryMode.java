package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TableHistoryMode implements IMenuEnum {
    BUY("📥 Покупка", 0),
    SELL("📤 Продажа", 0),
    // Назад
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    TableHistoryMode(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
