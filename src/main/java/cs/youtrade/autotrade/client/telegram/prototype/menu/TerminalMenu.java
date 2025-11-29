package cs.youtrade.autotrade.client.telegram.prototype.menu;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TerminalMenu implements IMenuEnum {
    RETURN("↩️ Назад", 0);

    private final String buttonName;
    private final int rowNum;
}
