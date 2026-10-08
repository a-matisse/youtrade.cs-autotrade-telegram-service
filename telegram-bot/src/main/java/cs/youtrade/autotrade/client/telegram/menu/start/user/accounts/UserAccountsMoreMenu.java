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
    INFO("📄 Данные", 2),
    PURCHASE_FREEZE("⏸️ Пауза покупок", 2),
    RETURN("↩️ Назад", 3),
    ACCOUNTS_MODE("Режим", 3),
    BACK("◀️ Обратно", 3);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserAccountsMoreMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
