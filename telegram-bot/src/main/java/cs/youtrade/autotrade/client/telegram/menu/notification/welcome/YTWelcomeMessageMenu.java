package cs.youtrade.autotrade.client.telegram.menu.notification.welcome;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum YTWelcomeMessageMenu implements IMenuEnum {
    TUTORIAL_URL("📖", "Туториал", 0),
    LANGUAGE("🌐", "Language", 1);

    private final String emoji;
    private final String label;
    private final String optionName;
    private final int rowNum;

    YTWelcomeMessageMenu(String emoji, String label, int rowNum) {
        this.emoji = emoji;
        this.label = label;
        this.optionName = UserMenu.NOTIFICATION_WELCOME.name() + ":" + name();
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
