package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.change;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TableChangeType implements IMenuEnum {
    SINGLE("📝 Одиночные", 0),
    GROUPED("📊 Групповые", 0);

    private final String buttonName;
    private final int rowNum;
}