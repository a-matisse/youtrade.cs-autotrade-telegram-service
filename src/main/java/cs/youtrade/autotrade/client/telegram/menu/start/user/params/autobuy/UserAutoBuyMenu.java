package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAutoBuyMenu implements IMenuEnum {
    // Изменить параметры AutoBuy
    AUTOBUY_UPDATE_FIELD("⚙️ Изменить параметры", 0),
    // Сменить тип функции
    AUTOBUY_SWITCH_FUNCTION_TYPE("🔄 Функцию", 1),
    // Сменить режим дублирования
    AUTOBUY_SWITCH_DUPLICATE_MODE("🔄 Дублирование", 1),
    // К параметрам Скоринг
    AUTOBUY_TO_SCORING("🔢 Scoring", 2),
    // К ключевым словам
    AUTOBUY_TO_WORDS("📚 Words", 2),
    // Получить список новых вещей
    AUTOBUY_GET_NEWEST_ITEMS("🌐 Общая история лотов", 3),
    // Назад (в PARAMS)
    RETURN("↩️ Назад", 5);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserAutoBuyMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
