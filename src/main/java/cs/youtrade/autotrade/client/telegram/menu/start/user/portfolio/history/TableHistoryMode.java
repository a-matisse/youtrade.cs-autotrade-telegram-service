package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TableHistoryMode implements IMenuEnum {
    BUY("📥 Покупка", 0),
    SELL("📤 Продажа", 0);

    private final String buttonName;
    private final int rowNum;
}
