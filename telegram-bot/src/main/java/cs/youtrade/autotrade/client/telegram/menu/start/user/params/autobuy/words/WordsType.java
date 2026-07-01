package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WordsType implements IMenuEnum {
    INCLUDED("✅ Включаемые слова", 0),
    EXCLUDED("🚫 Исключаемые слова", 1),
    // Назад
    RETURN("↩️ Назад", 3);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    WordsType(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
