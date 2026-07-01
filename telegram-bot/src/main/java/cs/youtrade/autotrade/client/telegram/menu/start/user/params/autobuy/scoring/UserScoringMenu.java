package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserScoringMenu implements IMenuEnum {
    // Добавить скоринг
    SCORING_ADD("➕ Добавить", 0),
    // Изменить скоринг
    SCORING_EDIT("✏️ Изменить", 0),
    // Удалить скоринг
    SCORING_REMOVE("🗑️ Удалить", 0),
    // Назад (в AUTOBUY)
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserScoringMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
