package cs.youtrade.autotrade.client.telegram.menu.main.delete.stage1;

import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeleteRequestMenu implements MenuEnumInterface {
    DELETE_CONFIRM("✅ Подтвердить удаление"),
    DELETE_DECLINE("❌ Отменить удаление");

    private final String buttonName;
}
