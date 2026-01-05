package cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.follow;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserFollowMenu implements IMenuEnum {
    FOLLOW_CHECK("👥 Заявки", 0),
    FOLLOW_FOLLOW("➕ Создать заявку", 1),
    FOLLOW_UNFOLLOW("🗑️ Удалить", 1),
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final int rowNum;
}
