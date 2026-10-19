package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractPcoTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.FunctionType;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
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
    private final BotTexts texts;

    public UserAutoBuyState(
            UserTextMessageSender sender,
            ParamsEndpoint paramsEndpoint,
            BotTexts texts
    ) {
        super(sender, texts);
        this.paramsEndpoint = paramsEndpoint;
        this.texts = texts;
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
    public UserAutoBuyMenu[] getOptions(UserData userData) {
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
        return getAutoBuyInfo(userData, fcd.getData());
    }

    private String getAutoBuyInfo(UserData userData, FcdParamsGetDto fcd) {
        var translations = botTexts.forUser(userData, "parameters.autobuy.UserAutoBuy");
        String buyWorksStr = getBuyWorksStr(fcd, translations);
        String correctionCoefficientMessage = getCorrectionCoeffStr(fcd, translations);
        String functionTypeStr = getFunctionTypeStr(fcd, translations);
        String duplicateStr = getDuplicateStr(fcd, translations);
        String followWorksStr = getFollowWorks(fcd, translations.inBundle("parameters.follow.AbstractPco"));

        return String.format("""
                        %s
                        
                        %s <b>%s</b>
                        <blockquote expandable>• %s
                        • %s: <b>%.2f%%</b>
                        • %s
                        • %s
                        • %s: <b>%.2f</b>
                        %s• %s: <b>%s</b>
                        • %s: <b>%s</b>
                        
                        %s</blockquote>
                        
                        %s <b>%s</b>
                        <blockquote>%s</blockquote>
                        
                        %s
                        %s
                        
                        %s
                        """,
                        fcd.getProfileStr(userData, texts.forUser(userData, "summary.ParamsSummary")),
                        DynamicEmoji.ITEM_RECEIVE.getEmoji(),
                        translations.get("buying-parameters"),
                        translations.format("price-from-to", Map.of("value1", String.format("<b>$%.2f</b>", fcd.getMinPrice()), "value2", String.format("<b>$%.2f</b>", fcd.getMaxPrice()))),
                        translations.get("price-multiplier"),
                        fcd.getPriceFactor() * 100,
                        translations.format("popularity-from-to", Map.of("value1", String.format("<b>%d</b>", fcd.getMinPopularity()), "value2", String.format("<b>%d</b>", fcd.getMaxPopularity()))),
                        translations.format("hold-period-from-to", Map.of("value1", String.format("<b>%s</b>", translations.format("short-days", Map.of("count", fcd.getMinDaysHold()))), "value2", String.format("<b>%s</b>", translations.format("short-days", Map.of("count", fcd.getMaxDaysHold()))))),
                        translations.get("manipulation-coefficient"),
                        fcd.getManipulationCoeff(),
                        correctionCoefficientMessage,
                        translations.get("function-type"),
                        functionTypeStr,
                        translations.get("duplication-mode"),
                        translations.inBundle("parameters.TradingOptions").enumText(fcd.getDuplicateMode(), "russian-name"),
                        duplicateStr,
                        DynamicEmoji.GRAPH_UP.getEmoji(),
                        translations.get("volume-estimate"),
                        fcd.getVolumeStr(translations.inBundle("summary.ParamsSummary")),
                        followWorksStr,
                        buyWorksStr,
                        fcd.getDirection());
    }

    private String getBuyWorksStr(FcdParamsGetDto fcd, LocalizedTexts translations) {
        return getWorksStr(fcd.getBuyWorks(), translations);
    }

    private String getWorksStr(boolean b, LocalizedTexts translations) {
        return b
                ? String.format("%s <b>%s</b>",
                        DynamicEmoji.ON.getEmoji(),
                        translations.get("buying-is-active"))
                : String.format("%s <b>%s</b>",
                        DynamicEmoji.OFF.getEmoji(),
                        translations.get("buying-is-inactive"));
    }

    private String getCorrectionCoeffStr(FcdParamsGetDto fcd, LocalizedTexts translations) {
        FunctionType functionType = fcd.getFunctionType();
        return (functionType != FunctionType.NONE && functionType != FunctionType.PREDICTIVE)
                ? String.format("""
                        • %s: <b>%.2f</b>
                        """,
                        translations.get("correction-coefficient"),
                        fcd.getCorrectionCoefficient())
                : "";
    }

    private String getFunctionTypeStr(FcdParamsGetDto fcd, LocalizedTexts translations) {
        return switch (fcd.getFunctionType()) {
            case LINEAR -> translations.get("linear");
            case EXPONENTIAL -> translations.get("exponential");
            case LOGARITHMIC -> translations.get("logarithmic");
            case PREDICTIVE -> translations.get("predictive");
            case NONE -> translations.get("not-set");
        };
    }

    private String getDuplicateStr(FcdParamsGetDto fcd, LocalizedTexts translations) {
        double maxDuplicates = fcd.getMaxDuplicates();
        int duplicateLag = fcd.getDuplicateLag();
        return maxDuplicates > 0
                ? String.format("%s %s",
                        DynamicEmoji.ON.getEmoji(),
                        translations.format("duplication-enabled-b-max-delay", Map.of("value1", String.format("<b>%s</b>", maxDuplicates), "value2", String.format("<b>%s</b>", translations.format("short-days", Map.of("count", duplicateLag))), "emphasis", String.format("<b>%s</b>", translations.get("duplication-enabled-b-max-delay.emphasis")))))
                : String.format("%s <b>%s</b>",
                        DynamicEmoji.OFF.getEmoji(),
                        translations.get("duplication-disabled"));
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
        return !fcd.getQcData().isExists();
    }
}
