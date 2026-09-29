package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAccountsMoreMenu implements IMenuEnum {
    RENAME("✏️ Переименовать", 0),
    REMOVE("🗑️ Удалить", 0),
    TRANSFER("✈️ Перенести", 1),
    RETURN("↩️ Назад", 2),
    ACCOUNTS_MODE("Режим", 2),
    BACK("◀️ Обратно", 2);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserAccountsMoreMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
