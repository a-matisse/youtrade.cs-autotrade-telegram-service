package cs.youtrade.autotrade.client.telegram.menu.start.user.params.delete.stage1;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeleteRequestMenu implements IMenuEnum {
    DELETE_CONFIRM("✅ Подтвердить удаление", 0),
    DELETE_DECLINE("❌ Отменить удаление", 0);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    DeleteRequestMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
