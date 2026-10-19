package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum UserLanguageMenu implements IMenuEnum {
    RU("", "Русский", 0),
    EN("", "English", 0),
    RETURN("↩️", "Назад", 1);

    private final String emoji;
    private final String label;
    private final String optionName;
    private final int rowNum;

    UserLanguageMenu(String emoji, String label, int rowNum) {
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
        return emoji.isEmpty() ? text : emoji + " " + text;
    }
}
