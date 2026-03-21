package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.check.stage1;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserFollowCheckMenu implements IMenuEnum {
    ACCEPT("✅ Принять", 0),
    DENY("❌ Отклонить", 0),
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserFollowCheckMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
