package cs.youtrade.autotrade.client.telegram.menu.start;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStartMenu implements IMenuEnum {
    USER("📊", "Начать торговлю", 0),
    REF("💎", "Реферальная программа", 1),
    TOP_UP("💳", "Пополнить", 2),
    GET_PRICE("💰", "Узнать цены", 2),
    GROUP_URL("📢", "Группа", 3),
    DOCS_URL("📖", "Туториал", 3),
    SUPPORT_URL("🆘", "Поддержка", 3);

    private final String emoji;
    private final String label;
    private final String optionName;
    private final int rowNum;

    UserStartMenu(String emoji, String label, int rowNum) {
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
