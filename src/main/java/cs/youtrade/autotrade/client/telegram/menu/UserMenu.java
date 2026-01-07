package cs.youtrade.autotrade.client.telegram.menu;

import lombok.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
public enum UserMenu {
    // reserved (menuId = 0)
    START(0, "/start", "Приветственное сообщение и запуск бота"),
    GET_PRICE,
    TOP_UP_STAGE_1,
    TOP_UP_STAGE_P,

    // main menu
    USER(1, "/user", "Главное меню"),
    USER_QUICK_CONFIG_INIT_STAGE_1,
    USER_QUICK_CONFIG_INIT_STAGE_2,
    USER_QUICK_CONFIG_INIT_STAGE_3,
    USER_QUICK_CONFIG_INIT_STAGE_P,
    USER_QUICK_CONFIG_DISABLE,
    USER_TOGGLE_AUTOBUY,
    USER_TOGGLE_AUTOSELL,

    // deep params menu
    PARAMS(2, "/params", "Управление параметрами"),
    PARAMS_RENAME_STAGE_1,
    PARAMS_RENAME_STAGE_2,
    PARAMS_RENAME_STAGE_P,
    PARAMS_LIST,
    PARAMS_SWITCH_STAGE_1,
    PARAMS_SWITCH_STAGE_P,
    PARAMS_CREATE_STAGE_1,
    PARAMS_CREATE_STAGE_2,
    PARAMS_CREATE_STAGE_P,
    PARAMS_DELETE_STAGE_1,
    PARAMS_DELETE_STAGE_P,

    TOKEN(3, "/token", "Управление токенами"),
    TOKEN_GET,
    TOKEN_ADD_STAGE_CHOOSE,
    TOKEN_ADD_STAGE_1,
    TOKEN_ADD_STAGE_2,
    TOKEN_ADD_STAGE_P,
    TOKEN_RENAME_STAGE_1,
    TOKEN_RENAME_STAGE_2,
    TOKEN_RENAME_STAGE_P,
    TOKEN_REMOVE_STAGE_CHOOSE,
    TOKEN_REMOVE_STAGE_1,
    TOKEN_REMOVE_STAGE_P,

    PORTFOLIO(4, "/portfolio", "Портфолио и история пользователя"),
    PORTFOLIO_SELLING_STAGE_1,
    PORTFOLIO_SELLING_STAGE_P,
    PORTFOLIO_WAITING,
    PORTFOLIO_HISTORY_STAGE_1,
    PORTFOLIO_HISTORY_STAGE_P,
    PORTFOLIO_UPLOAD_STAGE_1,
    PORTFOLIO_UPLOAD_STAGE_P,
    PORTFOLIO_CHANGE_STAGE_CHOOSE,
    PORTFOLIO_CHANGE_STAGE_1,
    PORTFOLIO_CHANGE_STAGE_P,
    PORTFOLIO_RESTRICT_STAGE_1,
    PORTFOLIO_RESTRICT_STAGE_P,

    AUTOBUY(5, "/autobuy", "Настройки автопокупки"),
    AUTOBUY_UPDATE_FIELD_STAGE_1,
    AUTOBUY_UPDATE_FIELD_STAGE_2,
    AUTOBUY_UPDATE_FIELD_STAGE_P,
    AUTOBUY_SWITCH_FUNCTION_TYPE,
    AUTOBUY_SWITCH_DUPLICATE_MODE,
    AUTOBUY_GET_NEWEST_ITEMS_STAGE_1,
    AUTOBUY_GET_NEWEST_ITEMS_STAGE_P,

    AUTOSELL(6, "/autosell", "Настройки автопродажи"),
    AUTOSELL_UPDATE_FIELD_STAGE_1,
    AUTOSELL_UPDATE_FIELD_STAGE_2,
    AUTOSELL_UPDATE_FIELD_STAGE_P,
    AUTOSELL_SWITCH_EVAL_MODE,
    AUTOSELL_SWITCH_EVAL_MODE_S1,

    SCORING(7, "/scoring", "Настройки скоринга"),
    SCORING_ADD_STAGE_1,
    SCORING_ADD_STAGE_2,
    SCORING_ADD_STAGE_P,
    SCORING_EDIT_STAGE_1,
    SCORING_EDIT_STAGE_2,
    SCORING_EDIT_STAGE_3,
    SCORING_EDIT_STAGE_P,
    SCORING_REMOVE_STAGE_1,
    SCORING_REMOVE_STAGE_P,

    WORDS(8, "/words", "Фильтры слов"),
    WORDS_ADD_STAGE_CHOOSE,
    WORDS_ADD_STAGE_1,
    WORDS_ADD_STAGE_P,
    WORDS_GET_STAGE_CHOOSE,
    WORDS_GET_STAGE_P,
    WORDS_REMOVE_STAGE_CHOOSE,
    WORDS_REMOVE_STAGE_1,
    WORDS_REMOVE_STAGE_P,
    WORDS_REMOVE_ALL_STAGE_CHOOSE,
    WORDS_REMOVE_ALL_STAGE_P,

    FOLLOW(9, "/follow", "Настройки следования за параметрами"),
    FOLLOW_CHECK,
    FOLLOW_CHECK_ACCEPT,
    FOLLOW_CHECK_DENY,
    FOLLOW_STAGE_CHOOSE,
    FOLLOW_STAGE_1,
    FOLLOW_STAGE_2,
    FOLLOW_STAGE_P,
    FOLLOW_UNFOLLOW_STAGE_1,
    FOLLOW_UNFOLLOW_STAGE_P,

    REF(10, "/referral", "Меню рефералов"),
    REF_CREATE,
    REF_CONNECT_STAGE_1,
    REF_CONNECT_STAGE_P;

    private int cmdId;
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
                .sorted(Comparator.comparingInt(UserMenu::getCmdId))
                .map(menu -> new BotCommand(menu.getTextCmd(), menu.cmdDescription))
                .toList();
    }

    UserMenu(int cmdId, String textCmd, String cmdDescription) {
        this.cmdId = cmdId;
        this.textCmd = textCmd;
        this.cmdDescription = cmdDescription;
    }

    public static UserMenu getByTextCmd(String cmd) {
        return menuMap.get(cmd.trim());
    }

    public static List<BotCommand> getCommands() {
        return cmdList;
    }
}
