package cs.youtrade.autotrade.client.telegram.menu.main.params.follow;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserFollowOperationType implements IMenuEnum {
    FOLLOW("👥 Подписаться"),
    COPY("📋 Копировать стратегию");

    private final String buttonName;
}