package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.SellPriceEvalMode;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserAutoSellState extends AbstractTextMenuState<UserAutoSellMenu> {
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
            case AUTOSELL_TOGGLE_AUTOSELL -> UserMenu.AUTOSELL_TOGGLE_AUTOSELL;
            case AUTOSELL_TO_TABLES -> UserMenu.TABLE;
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

        return getAutoSellInfo(fcd.getData());
    }


    private String getAutoSellInfo(FcdParamsGetDto fcd) {
        String sellWorksStr = getSellWorksStr(fcd);
        String evalModeStr = getEvalModeStr(fcd);

        return String.format("""
                        params-ID=%s
                        Имя: %s
                        
                        %s
                        🏁 Пункт назначения продажи: %s
                        
                        Параметры автопродажи:
                        🏷️ Минимальная прибыльность: %.2f%%
                        🏷️ Максимальная прибыльность: %.2f%%
                        
                        🔎 Режим оценки: %s
                        """,
                fcd.getTdpId(),
                fcd.getGivenName(),
                sellWorksStr,
                fcd.getDestination(),
                fcd.getMinSellProfit() * 100,
                fcd.getMaxSellProfit() * 100,
                evalModeStr
        );
    }

    private String getSellWorksStr(FcdParamsGetDto tdp) {
        return getWorksStr(tdp.getSellWorks());
    }

    private String getWorksStr(boolean b) {
        return b ? "🟢 Работает" : "🔴 Не работает";
    }

    private String getEvalModeStr(FcdParamsGetDto tdp) {
        SellPriceEvalMode mode = tdp.getEvalMode();
        Integer suggEvalModeC1 = tdp.getSuggEvalModeC1();

        if (mode == null) return "—";
        return switch (mode) {
            case DEFAULT -> "Стандартный";
            case INTELLIGENT_V1 -> {
                int sugg = (suggEvalModeC1 != null) ? suggEvalModeC1 : 50;
                yield String.format("""
                                Intelligent_V1 (рек. evalModeC1: %d)
                                🔢 Параметр evalModeC1: %d
                                """,
                        sugg,
                        tdp.getEvalModeC1()
                );
            }
        };
    }
}
