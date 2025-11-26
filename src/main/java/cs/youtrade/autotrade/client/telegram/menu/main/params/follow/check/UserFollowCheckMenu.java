package cs.youtrade.autotrade.client.telegram.menu.main.params.follow.check;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserFollowCheckMenu implements IMenuEnum {
    ACCEPT("✅ Принять"),
    DENY("❌ Отклонить"),
    RETURN("↩️ Назад");

    private final String buttonName;
}
