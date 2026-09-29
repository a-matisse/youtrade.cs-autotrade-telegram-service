package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MarketCsgoSendMenu implements IMenuEnum {
    AMOUNT_200("200 USD", 0),
    AMOUNT_500("500 USD", 0),
    AMOUNT_1000("1000 USD", 1),
    ALL("Всё", 1),
    CONFIRM("✅ Подтвердить", 0),
    CANCEL("❌ Отменить", 0),
    RECEIVED("✅ Деньги пришли", 0),
    NOT_RECEIVED("❌ Не пришли", 0),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    MarketCsgoSendMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
