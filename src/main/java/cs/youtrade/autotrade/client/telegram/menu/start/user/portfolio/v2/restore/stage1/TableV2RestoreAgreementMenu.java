package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.restore.stage1;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TableV2RestoreAgreementMenu implements IMenuEnum {
    AGREE("✅ Я согласен с условиями восстановления", 0),
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    TableV2RestoreAgreementMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
