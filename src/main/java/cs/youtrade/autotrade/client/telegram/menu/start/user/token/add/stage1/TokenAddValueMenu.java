package cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.stage1;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenAddValueMenu implements IMenuEnum {
    GET_API("Узнать API-ключ"),
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
