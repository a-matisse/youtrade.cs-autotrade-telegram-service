package cs.youtrade.autotrade.client.telegram.prototype.menu;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TerminalMenu implements IMenuEnum {
    RETURN("↩️ Назад", 0);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    TerminalMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
