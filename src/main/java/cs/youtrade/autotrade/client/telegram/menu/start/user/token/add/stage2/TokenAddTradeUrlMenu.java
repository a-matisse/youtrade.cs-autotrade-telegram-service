package cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.stage2;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenAddTradeUrlMenu implements IMenuEnum {
    GET_TRADE("Узнать Trade-ссылку"),
    RETURN("↩️ Назад");

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    TokenAddTradeUrlMenu(
            String buttonName
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = ordinal();
    }
}
