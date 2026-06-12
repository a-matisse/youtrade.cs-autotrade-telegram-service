package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenDeleteOption implements IMenuEnum {
    SINGLE("🗑️ Одиночное", 0),
    ALL("💥 Массовое", 0),
    // Назад
    RETURN("↩️ Назад", 3);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    TokenDeleteOption(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
