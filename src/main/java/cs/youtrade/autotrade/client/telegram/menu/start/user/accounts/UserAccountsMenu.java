package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAccountsMenu implements IMenuEnum {
    ACCOUNTS_PREVIOUS("◀️ Предыдущая", 0),
    ACCOUNTS_MODE("Режим", 0),
    ACCOUNTS_NEXT("Следующая ▶️", 0),
    ACCOUNTS_ADD("➕ Добавить", 1),
    ACCOUNTS_REMOVE("🗑️ Удалить", 1),
    ACCOUNTS_RENAME("✏️ Переименовать", 1),
    RETURN("↩️ Назад", 2);

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
