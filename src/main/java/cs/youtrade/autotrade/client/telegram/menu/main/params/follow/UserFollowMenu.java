package cs.youtrade.autotrade.client.telegram.menu.main.params.follow;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserFollowMenu implements IMenuEnum {
    FOLLOW_CHECK("📋 Посмотреть заявки"),
    FOLLOW_FOLLOW("👥 Подписаться / Копировать"),
    FOLLOW_UNFOLLOW("🗑️ Удалить подписку"),
    RETURN("↩️ Назад");

    private final String buttonName;
}
