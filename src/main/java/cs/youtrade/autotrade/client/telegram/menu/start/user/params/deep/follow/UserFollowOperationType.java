package cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.follow;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserFollowOperationType implements IMenuEnum {
    FOLLOW("👥 Подписаться", 0),
    COPY("📋 Копировать", 0);

    private final String buttonName;
    private final int rowNum;
}