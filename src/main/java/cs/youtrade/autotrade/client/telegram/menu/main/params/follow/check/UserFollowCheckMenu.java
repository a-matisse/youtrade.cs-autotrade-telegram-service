package cs.youtrade.autotrade.client.telegram.menu.main.params.follow.check;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserFollowCheckMenu implements IMenuEnum {
    ACCEPT("✅ Принять", 0),
    DENY("❌ Отклонить", 0),
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final int rowNum;
}
