package cs.youtrade.autotrade.client.telegram.menu.start.user.token;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTokensMenu implements IMenuEnum {
    TOKEN_ADD("➕ Добавить", 0),
    TOKEN_REMOVE("🗑️ Удалить", 0),
    TOKEN_RENAME("✏️ Переименовать", 0),
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserTokensMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
