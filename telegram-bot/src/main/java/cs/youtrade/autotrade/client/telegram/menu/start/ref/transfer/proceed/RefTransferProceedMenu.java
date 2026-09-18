package cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer.proceed;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RefTransferProceedMenu implements IMenuEnum {
    RETRY("🔄 Повторить запрос", 0),
    RETURN("↩️ В кабинет", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    RefTransferProceedMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
