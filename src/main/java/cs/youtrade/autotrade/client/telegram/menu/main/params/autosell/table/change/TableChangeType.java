package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.change;

import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TableChangeType implements MenuEnumInterface {
    SINGLE("📝 Одиночные изменения"),
    GROUPED("📊 Групповые изменения");

    private final String buttonName;
}