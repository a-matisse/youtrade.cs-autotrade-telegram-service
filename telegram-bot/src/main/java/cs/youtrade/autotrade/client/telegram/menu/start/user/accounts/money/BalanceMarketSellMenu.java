package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BalanceMarketSellMenu implements IMenuEnum {
    AMOUNT_500("500 USD", 0),
    AMOUNT_1000("1000 USD", 0),
    ALL("Всё", 1),
    USDT_TRC20("USDT TRC-20", 0),
    ADDRESS_CHANGE("✏️ Изменить", 0),
    CONFIRM("✅ Подтвердить", 0),
    REFRESH("🔄 Обновить", 0),
    CANCEL_SESSION("❌ Отменить заявку", 1),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    BalanceMarketSellMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
