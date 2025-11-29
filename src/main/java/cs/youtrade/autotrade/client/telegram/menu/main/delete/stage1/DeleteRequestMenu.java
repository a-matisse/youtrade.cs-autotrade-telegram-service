package cs.youtrade.autotrade.client.telegram.menu.main.delete.stage1;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeleteRequestMenu implements IMenuEnum {
    DELETE_CONFIRM("✅ Подтвердить удаление", 0),
    DELETE_DECLINE("❌ Отменить удаление", 0);

    private final String buttonName;
    private final int rowNum;
}
