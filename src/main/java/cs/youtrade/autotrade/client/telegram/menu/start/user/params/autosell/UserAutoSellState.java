package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractPcoTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.SellPriceEvalMode;
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
public class UserAutoSellState extends AbstractPcoTextMenuState<UserAutoSellMenu> {
    private final Map<UserData, FcdParamsGetDto> paramsData = new ConcurrentHashMap<>();
    private final ParamsEndpoint endpoint;

    public UserAutoSellState(
            UserTextMessageSender sender,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOSELL;
    }

    @Override
    public UserAutoSellMenu getOption(String optionStr) {
        return UserAutoSellMenu.valueOf(optionStr);
    }

    @Override
    public UserAutoSellMenu[] getOptions() {
        return UserAutoSellMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, UserAutoSellMenu t) {
        return switch (t) {
            case AUTOSELL_UPDATE_FIELD -> UserMenu.AUTOSELL_UPDATE_FIELD_STAGE_1;
            case AUTOSELL_SWITCH_EVAL_MODE -> UserMenu.AUTOSELL_SWITCH_EVAL_MODE;
            case AUTOSELL_SWITCH_EVAL_S1 -> UserMenu.AUTOSELL_SWITCH_EVAL_MODE_S1;
            case RETURN -> UserMenu.PARAMS;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var restAns = endpoint.getCurrent(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        paramsData.put(user, fcd.getData());
        return getAutoSellInfo(fcd.getData());
    }


    private String getAutoSellInfo(FcdParamsGetDto fcd) {
        String sellWorksStr = getSellWorksStr(fcd);
        String evalModeStr = getEvalModeStr(fcd);
        String followWorksStr = getFollowWorks(fcd);

        return String.format("""
                        %s
                        ━━━━━━━━━━━
                        
                        %s
                        
                        📥 <b>Параметры продажи</b>
                        <blockquote expandable>• Прибыльность: от <b>%.2f%%</b> до <b>%.2f%%</b>
                        • Режим оценки: %s</blockquote>
                        
                        %s
                        
                        <b>%s</b> → <b>%s</b>
                        """,
                sellWorksStr,
                fcd.getProfileStr(),
                fcd.getMinSellProfit() * 100,
                fcd.getMaxSellProfit() * 100,
                evalModeStr,
                followWorksStr,
                fcd.getSource().getMarketName(),
                fcd.getDestination().getMarketName()
        );
    }

    private String getSellWorksStr(FcdParamsGetDto tdp) {
        return getWorksStr(tdp.getSellWorks());
    }

    private String getWorksStr(boolean b) {
        return b ? "🟢 <b>Продажа работает</b>" : "🔴 <b>Продажа не работает</b>";
    }

    private String getEvalModeStr(FcdParamsGetDto fcd) {
        SellPriceEvalMode mode = fcd.getEvalMode();
        Integer suggEvalModeC1 = fcd.getSuggEvalModeC1();

        if (mode == null) return "—";
        return switch (mode) {
            case DEFAULT -> "<b>Стандартный</b>";
            case INTELLIGENT_V1 -> {
                int sugg = (suggEvalModeC1 != null) ? suggEvalModeC1 : 50;
                yield String.format("""
                                <b>Intelligent v1</b>
                                • Параметр evalModeC1: <b>%d</b> (рек. <b>%d</b>)
                                
                                <b>%s</b>""",
                        fcd.getEvalModeC1(),
                        sugg,
                        evalModeS1WorksStr(fcd)
                );
            }
        };
    }

    private String evalModeS1WorksStr(FcdParamsGetDto fcd) {
        return fcd.getEvalModeS1()
                ? "✅ EvalModeS1 работает"
                : "❌ EvalModeS1 не работает";
    }

    @Override
    public List<ParamsCopyOptions> getMenuPcos() {
        return List.of(ParamsCopyOptions.AUTOSELL);
    }

    @Override
    public Map<UserAutoSellMenu, Predicate<UserData>> getVisibilityPredicates(UserData user) {
        return Map.of(
                UserAutoSellMenu.AUTOSELL_UPDATE_FIELD, this::changeOn
        );
    }

    private boolean changeOn(UserData user) {
        var fcd = paramsData.get(user);
        return !fcd.getConfigExists();
    }
}
