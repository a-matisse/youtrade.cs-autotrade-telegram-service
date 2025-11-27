package cs.youtrade.autotrade.client.telegram.menu.main.params.token.delete;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenDeleteOption implements IMenuEnum {
    SINGLE("🗑️ Удалить один"),
    ALL("💥 Удалить все");

    private final String buttonName;
}
