package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.buyer.stage2;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountsAddTradeUrlMenu implements IMenuEnum {
    GET_TRADE("❓ Где я могу узнать Trade-ссылку"),
    RETURN("↩️ Назад");

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    AccountsAddTradeUrlMenu(
            String buttonName
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = ordinal();
    }
}
