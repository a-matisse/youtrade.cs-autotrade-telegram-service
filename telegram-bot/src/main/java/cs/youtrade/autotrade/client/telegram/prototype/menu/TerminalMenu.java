package cs.youtrade.autotrade.client.telegram.prototype.menu;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum TerminalMenu implements IMenuEnum {
    RETURN("↩️", "Назад", 0);

    private final String emoji;
    private final String label;
    private final String optionName;
    private final int rowNum;

    TerminalMenu(String emoji, String label, int rowNum) {
        this.emoji = emoji;
        this.label = label;
        this.optionName = name();
        this.rowNum = rowNum;
    }

    @Override
    public String getButtonName() {
        return getButtonName(label);
    }

    public String getButtonName(String text) {
        return emoji + " " + text;
    }
}
