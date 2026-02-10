package cs.youtrade.autotrade.client.telegram.menu.start.user.token.delete;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenDeleteOption implements IMenuEnum {
    SINGLE("🗑️ Одиночное", 0),
    ALL("💥 Массовое", 0),
    // Назад
    RETURN("↩️ Назад", 3);

    private final String buttonName;
    private final int rowNum;
}
