package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.api;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenAddValueMenu implements IMenuEnum {
    GET_API("❓ Где я могу найти API-ключ"),
    RETURN("↩️ Назад");

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    TokenAddValueMenu(
            String buttonName
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = ordinal();
    }
}
