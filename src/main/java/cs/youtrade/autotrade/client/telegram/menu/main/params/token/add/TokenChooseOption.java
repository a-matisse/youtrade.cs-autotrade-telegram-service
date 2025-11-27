package cs.youtrade.autotrade.client.telegram.menu.main.params.token.add;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenChooseOption implements IMenuEnum {
    BUY_TOKEN("📥 Токен покупки"),
    SELL_TOKEN("📤 Токен продажи");

    private final String buttonName;
}
