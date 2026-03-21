package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow;

import cs.youtrade.telegram.buttons.IMenuEnum;
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
    private final String optionName;
    private final int rowNum;

    UserFollowMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
