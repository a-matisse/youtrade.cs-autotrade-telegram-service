package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractPcoTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.FunctionType;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Service
public class UserAutoBuyState extends AbstractPcoTextMenuState<UserAutoBuyMenu> {
    private final Map<UserData, FcdParamsGetDto> paramsData = new ConcurrentHashMap<>();
    private final ParamsEndpoint paramsEndpoint;

    public UserAutoBuyState(
            UserTextMessageSender sender,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY;
    }

    @Override
    public UserAutoBuyMenu getOption(String optionStr) {
        return UserAutoBuyMenu.valueOf(optionStr);
    }

    @Override
    public UserAutoBuyMenu[] getOptions() {
        return UserAutoBuyMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserAutoBuyMenu t) {
        return switch (t) {
            case AUTOBUY_UPDATE_FIELD -> UserMenu.AUTOBUY_UPDATE_FIELD_STAGE_1;
            case AUTOBUY_SWITCH_FUNCTION_TYPE -> UserMenu.AUTOBUY_SWITCH_FUNCTION_TYPE;
            case AUTOBUY_SWITCH_DUPLICATE_MODE -> UserMenu.AUTOBUY_SWITCH_DUPLICATE_MODE;
            case AUTOBUY_TO_SCORING -> UserMenu.SCORING;
            case AUTOBUY_TO_WORDS -> UserMenu.WORDS;
            case AUTOBUY_GET_NEWEST_ITEMS -> UserMenu.AUTOBUY_GET_NEWEST_ITEMS_STAGE_1;
            case RETURN -> UserMenu.PARAMS;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var restAns = paramsEndpoint.getCurrent(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        paramsData.put(userData, fcd.getData());
        return getAutoBuyInfo(fcd.getData());
    }

    private String getAutoBuyInfo(FcdParamsGetDto fcd) {
        String buyWorksStr = getBuyWorksStr(fcd);
        String correctionCoefficientMessage = getCorrectionCoeffStr(fcd);
        String functionTypeStr = getFunctionTypeStr(fcd);
        String duplicateStr = getDuplicateStr(fcd);
        String followWorksStr = getFollowWorks(fcd);

        return String.format("""
                Имя: %s
                🆔 params-ID=%s
                
                %s
                🔍 Источник закупки: %s
                
                Параметры автопокупки:
                <blockquote expandable>🛒 Минимальная цена: $%.2f
                🛒 Максимальная цена: $%.2f
                ⚖️ Множитель цены: %.2f%%
                📊 Минимальная популярность: %d
                📊 Максимальная популярность: %d
                ⏳ Минимум дней удержания: %d
                ⏳ Максимум дней удержания: %d
                ⚙️ Коэффициент манипуляции: %.2f
                %s📐 Тип функции: %s
                🔄 Режим дублирования: %s
                %s</blockquote>
                
                Оценка объема:
                <blockquote>%s</blockquote>
                
                %s
                """,
                fcd.getGivenName(),
                fcd.getTdpId(),
                buyWorksStr,
                fcd.getSource(),
                fcd.getMinPrice(),
                fcd.getMaxPrice(),
                fcd.getPriceFactor() * 100,
                fcd.getMinPopularity(),
                fcd.getMaxPopularity(),
                fcd.getMinDaysHold(),
                fcd.getMaxDaysHold(),
                fcd.getManipulationCoeff(),
                correctionCoefficientMessage,
                functionTypeStr,
                fcd.getDuplicateMode().getRussianName(),
                duplicateStr,
                fcd.getVolumeStr(),
                followWorksStr
        );
    }

    private String getBuyWorksStr(FcdParamsGetDto fcd) {
        return getWorksStr(fcd.getBuyWorks());
    }

    private String getWorksStr(boolean b) {
        return b ? "🟢 Работает" : "🔴 Не работает";
    }

    private String getCorrectionCoeffStr(FcdParamsGetDto fcd) {
        FunctionType functionType = fcd.getFunctionType();
        return (functionType != FunctionType.NONE && functionType != FunctionType.PREDICTIVE)
                ? String.format("🔧 Коэффициент коррекции: %.2f\n", fcd.getCorrectionCoefficient())
                : "";
    }

    private String getFunctionTypeStr(FcdParamsGetDto fcd) {
        return switch (fcd.getFunctionType()) {
            case LINEAR -> "Линейная";
            case EXPONENTIAL -> "Экспоненциальная";
            case LOGARITHMIC -> "Логарифмическая";
            case PREDICTIVE -> "Прогнозная";
            case NONE -> "Не задана";
        };
    }

    private String getDuplicateStr(FcdParamsGetDto fcd) {
        double maxDuplicates = fcd.getMaxDuplicates();
        int duplicateLag = fcd.getDuplicateLag();
        return maxDuplicates > 0 ?
                "Дублирование включено 🔄 (макс.: " + maxDuplicates + ", задержка: " + duplicateLag + " дн.)" :
                "Дублирование выключено 🚫";
    }

    @Override
    public List<ParamsCopyOptions> getMenuPcos() {
        return List.of(ParamsCopyOptions.AUTOBUY);
    }

    @Override
    public Map<UserAutoBuyMenu, Predicate<UserData>> getVisibilityPredicates(UserData user) {
        return Map.of(
                UserAutoBuyMenu.AUTOBUY_UPDATE_FIELD, this::changeOn
        );
    }

    private boolean changeOn(UserData user) {
        var fcd = paramsData.get(user);
        return !fcd.getConfigExists();
    }
}
