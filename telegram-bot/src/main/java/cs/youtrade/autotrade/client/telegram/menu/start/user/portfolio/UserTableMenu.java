package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTableMenu implements IMenuEnum {
    TABLE_INVENTORY("🎒 Инвентарь", 0),
    TABLE_SELLING("💵 Витрина", 0),
    TABLE_WAITING("🕒 Ожидание", 1),
    RESTORE("🔁 Восстановить", 1),
    TABLE_HISTORY("🗂️ История", 1),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserTableMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
