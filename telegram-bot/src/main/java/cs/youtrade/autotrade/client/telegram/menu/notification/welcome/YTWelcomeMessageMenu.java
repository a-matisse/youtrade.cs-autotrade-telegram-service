package cs.youtrade.autotrade.client.telegram.menu.notification.welcome;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;

@Getter
public enum YTWelcomeMessageMenu implements IMenuEnum {
    TUTORIAL_URL("📖 Туториал", 0),
    LANGUAGE("🌐 Language", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    YTWelcomeMessageMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = UserMenu.NOTIFICATION_WELCOME.name() + ":" + name();
        this.rowNum = rowNum;
    }
}
