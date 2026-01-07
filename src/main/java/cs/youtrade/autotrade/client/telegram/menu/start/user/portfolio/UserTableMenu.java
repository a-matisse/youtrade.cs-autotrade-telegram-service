package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTableMenu implements IMenuEnum {
    TABLE_SELLING("В продаже", 0),
    TABLE_WAITING("В ожидании", 0),
    TABLE_HISTORY("История", 1),
    TABLE_UPLOAD("Выставить", 1),
    TABLE_CHANGE("Изменить", 1),
    TABLE_RESTRICT("🚫 Запретить", 2),
    RETURN("↩️ Назад", 3);

    private final String buttonName;
    private final int rowNum;
}
