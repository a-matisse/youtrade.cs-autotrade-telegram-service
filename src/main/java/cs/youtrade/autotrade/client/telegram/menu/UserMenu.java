package cs.youtrade.autotrade.client.telegram.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public enum UserMenu {
    // reserved (menuId = 0)
    START(0, "/start", "Приветственное сообщение и запуск бота"),
    GET_PRICE(0),
    TOP_UP_STAGE_1(0),
    TOP_UP_STAGE_P(0),

    // main menu
    MAIN(1, "/menu", "Главное меню"),
    MAIN_PARAMETERS_LIST(1),
    MAIN_PARAMETERS_SWITCH_STAGE_1(1),
    MAIN_PARAMETERS_SWITCH_STAGE_P(1),
    MAIN_PARAMETERS_CREATE_STAGE_1(1),
    MAIN_PARAMETERS_CREATE_STAGE_2(1),
    MAIN_PARAMETERS_CREATE_STAGE_P(1),
    MAIN_PARAMETERS_DELETE_STAGE_1(1),
    MAIN_PARAMETERS_DELETE_STAGE_P(1),
    MAIN_GET_NEWEST_ITEMS_STAGE_1(1),
    MAIN_GET_NEWEST_ITEMS_STAGE_P(1),

    PARAMS(2, "/params", "Управление параметрами"),
    PARAMS_RENAME_STAGE_1(2),
    PARAMS_RENAME_STAGE_2(2),
    PARAMS_RENAME_STAGE_P(2),

    FOLLOW(9, "/follow", "Настройки следования за параметрами"),
    FOLLOW_CHECK(9),
    FOLLOW_STAGE_CHOOSE(9),
    FOLLOW_STAGE_1(9),
    FOLLOW_STAGE_2(9),
    FOLLOW_STAGE_P(9),
    FOLLOW_UNFOLLOW_STAGE_1(9),
    FOLLOW_UNFOLLOW_STAGE_P(9),

    TOKEN(3, "/token", "Управление токенами"),
    TOKEN_GET(3),
    TOKEN_ADD_STAGE_CHOOSE(3),
    TOKEN_ADD_STAGE_1(3),
    TOKEN_ADD_STAGE_2(3),
    TOKEN_ADD_STAGE_P(3),
    TOKEN_RENAME_STAGE_1(3),
    TOKEN_RENAME_STAGE_2(3),
    TOKEN_RENAME_STAGE_P(3),
    TOKEN_REMOVE_STAGE_CHOOSE(3),
    TOKEN_REMOVE_STAGE_1(3),
    TOKEN_REMOVE_STAGE_P(3),

    AUTOBUY(4, "/autobuy", "Настройки автопокупки"),
    AUTOBUY_UPDATE_FIELD_STAGE_1(4),
    AUTOBUY_UPDATE_FIELD_STAGE_2(4),
    AUTOBUY_UPDATE_FIELD_STAGE_P(4),
    AUTOBUY_SWITCH_FUNCTION_TYPE(4),
    AUTOBUY_SWITCH_DUPLICATE_MODE(4),
    AUTOBUY_TOGGLE_AUTOBUY(4),

    SCORING(5, "/scoring", "Настройки скоринга"),
    SCORING_ADD_STAGE_1(5),
    SCORING_ADD_STAGE_2(5),
    SCORING_ADD_STAGE_P(5),
    SCORING_EDIT_STAGE_1(5),
    SCORING_EDIT_STAGE_2(5),
    SCORING_EDIT_STAGE_3(5),
    SCORING_EDIT_STAGE_P(5),
    SCORING_REMOVE_STAGE_1(5),
    SCORING_REMOVE_STAGE_P(5),

    WORDS(6, "/words", "Фильтры слов"),
    WORDS_ADD_STAGE_CHOOSE(6),
    WORDS_ADD_STAGE_1(6),
    WORDS_ADD_STAGE_P(6),
    WORDS_GET_STAGE_CHOOSE(6),
    WORDS_GET_STAGE_P(6),
    WORDS_REMOVE_STAGE_CHOOSE(6),
    WORDS_REMOVE_STAGE_1(6),
    WORDS_REMOVE_STAGE_P(6),
    WORDS_REMOVE_ALL_STAGE_CHOOSE(6),
    WORDS_REMOVE_ALL_STAGE_P(6),

    AUTOSELL(7, "/autosell", "Настройки автопродажи"),
    AUTOSELL_UPDATE_FIELD_STAGE_1(7),
    AUTOSELL_UPDATE_FIELD_STAGE_2(7),
    AUTOSELL_UPDATE_FIELD_STAGE_P(7),
    AUTOSELL_SWITCH_EVAL_MODE(7),
    AUTOSELL_SWITCH_EVAL_MODE_S1(7),
    AUTOSELL_TOGGLE_AUTOSELL(7),

    TABLE(8, "/table", "Таблица продаж"),
    TABLE_SELLING_STAGE_1(8),
    TABLE_SELLING_STAGE_P(8),
    TABLE_WAITING(8),
    TABLE_HISTORY_STAGE_1(8),
    TABLE_HISTORY_STAGE_P(8),
    TABLE_UPLOAD_STAGE_1(8),
    TABLE_UPLOAD_STAGE_P(8),
    TABLE_CHANGE_STAGE_CHOOSE(8),
    TABLE_CHANGE_STAGE_1(8),
    TABLE_CHANGE_STAGE_P(8),
    TABLE_RESTRICT_STAGE_1(8),
    TABLE_RESTRICT_STAGE_P(8);

    private final int menuId;
    private String textCmd;
    private String cmdDescription;

    private static final Map<String, UserMenu> menuMap;
    private static final List<BotCommand> cmdList;

    static {
        menuMap = Arrays
                .stream(UserMenu.values())
                .filter(menu -> menu.textCmd != null)
                .collect(Collectors.toMap(UserMenu::getTextCmd, Function.identity()));

        cmdList = Arrays
                .stream(UserMenu.values())
                .filter(menu -> menu.textCmd != null)
                .map(menu -> new BotCommand(menu.getTextCmd(), menu.cmdDescription))
                .toList();
    }

    public static UserMenu getByTextCmd(String cmd) {
        return menuMap.get(cmd.trim());
    }

    public static List<BotCommand> getCommands() {
        return cmdList;
    }
}
