package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountsAddChooseOption implements IMenuEnum {
    BUYER_ACCOUNT("📥 Покупка", 0),
    SELLER_ACCOUNT("📤 Продажа", 0),
    WORKER_ACCOUNT("🚚 Воркер", 0),
    // Назад
    RETURN("↩️ Назад", 3);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    AccountsAddChooseOption(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
