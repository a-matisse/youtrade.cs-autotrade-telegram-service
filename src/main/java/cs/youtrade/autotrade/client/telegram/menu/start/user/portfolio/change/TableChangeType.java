package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TableChangeType implements IMenuEnum {
    SINGLE("📝 Одиночные", 0),
    GROUPED("📊 Групповые", 0),
    // Назад
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    TableChangeType(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}