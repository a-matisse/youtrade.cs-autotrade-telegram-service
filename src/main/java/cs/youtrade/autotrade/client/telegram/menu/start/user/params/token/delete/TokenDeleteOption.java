package cs.youtrade.autotrade.client.telegram.menu.start.user.params.token.delete;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenDeleteOption implements IMenuEnum {
    SINGLE("🗑️ Удалить один", 0),
    ALL("💥 Удалить все", 0);

    private final String buttonName;
    private final int rowNum;
}
