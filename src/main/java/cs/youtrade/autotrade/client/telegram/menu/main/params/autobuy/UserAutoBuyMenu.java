package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAutoBuyMenu implements IMenuEnum {
    // Изменить параметры AutoBuy
    AUTOBUY_UPDATE_FIELD("⚙️ Изменить параметры AutoBuy"),
    // Сменить тип функции
    AUTOBUY_SWITCH_FUNCTION_TYPE("🔄 Сменить тип функции"),
    // Сменить режим дублирования
    AUTOBUY_SWITCH_DUPLICATE_MODE("🔄 Сменить режим дублирования"),
    // Вкл/Выкл автопокупку
    AUTOBUY_TOGGLE_AUTOBUY("🚀 Вкл/Выкл AutoBuy"),
    // К параметрам Скоринг
    AUTOBUY_TO_SCORING("🔢 Скоринг"),
    // К ключевым словам
    AUTOBUY_TO_WORDS("🔍 К ключевым словам"),
    // Назад (в PARAMS)
    RETURN("↩️ Назад");

    private final String buttonName;
}
