package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.check.stage1;

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
