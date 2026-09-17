package cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer.confirm;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RefTransferConfirmMenu implements IMenuEnum {
    CONFIRM("✅ Подтвердить перевод", 0),
    CANCEL("❌ Отменить", 0);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    RefTransferConfirmMenu(String buttonName, int rowNum) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
