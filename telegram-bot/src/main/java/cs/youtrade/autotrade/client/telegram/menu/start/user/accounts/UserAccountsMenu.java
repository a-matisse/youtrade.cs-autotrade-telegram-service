package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAccountsMenu implements IMenuEnum {
    ACCOUNTS_PREVIOUS("◀️", 0),
    ACCOUNTS_MODE("Режим", 0),
    ACCOUNTS_NEXT("▶️", 0),
    ACCOUNTS_ADD("➕ Добавить", 2),
    ACCOUNTS_TRANSFER("✈️ Перенести", 2),
    ACCOUNTS_RENAME("✏️ Сменить имя", 2),
    ACCOUNTS_REMOVE("🗑️ Удалить", 2),
    RETURN("↩️ Назад", 3);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserAccountsMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
