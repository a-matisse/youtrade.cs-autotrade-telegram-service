package cs.youtrade.autotrade.client.telegram.menu.main.params.follow;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum
UserFollowMenu implements IMenuEnum {
    FOLLOW_FOLLOW("➕ Добавить следование"),
    FOLLOW_UNFOLLOW("🗑️ Удалить следование"),
    FOLLOW_COPY("📋 Копировать настройки"),
    RETURN("↩️ Назад");

    private final String buttonName;
}
