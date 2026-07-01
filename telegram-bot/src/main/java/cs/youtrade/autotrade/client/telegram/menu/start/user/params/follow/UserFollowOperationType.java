package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserFollowOperationType implements IMenuEnum {
    FOLLOW("👥 Подписаться", 0),
    COPY("📋 Копировать", 0);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserFollowOperationType(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}