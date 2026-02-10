package cs.youtrade.autotrade.client.telegram.menu.start.user.token.add;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenChooseOption implements IMenuEnum {
    BUY_TOKEN("📥 Покупка", 0),
    SELL_TOKEN("📤 Продажа", 0),
    // Назад
    RETURN("↩️ Назад", 3);

    private final String buttonName;
    private final int rowNum;
}
