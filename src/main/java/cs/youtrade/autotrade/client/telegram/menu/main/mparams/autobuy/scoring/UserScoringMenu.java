package cs.youtrade.autotrade.client.telegram.menu.main.mparams.autobuy.scoring;

import cs.youtrade.autotrade.client.telegram.prototype.MenuEnumInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserScoringMenu implements MenuEnumInterface {
    // Добавить скоринг
    SCORING_ADD("➕ Добавить правило"),
    // Изменить скоринг
    SCORING_EDIT("✏️ Редактировать правило"),
    // Удалить скоринг
    SCORING_REMOVE("🗑️ Удалить правило"),
    // Назад (в AUTOBUY)
    RETURN("↩️ Назад");

    private final String buttonName;
}
