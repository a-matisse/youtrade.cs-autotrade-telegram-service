package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractPcoTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.SellPriceEvalMode;
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
public class UserAutoSellState extends AbstractPcoTextMenuState<UserAutoSellMenu> {
    private final Map<UserData, FcdParamsGetDto> paramsData = new ConcurrentHashMap<>();
    private final ParamsEndpoint endpoint;
    private final BotTexts texts;

    public UserAutoSellState(
            UserTextMessageSender sender,
            ParamsEndpoint endpoint,
            BotTexts texts
    ) {
        super(sender, texts);
        this.endpoint = endpoint;
        this.texts = texts;
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
    public UserAutoSellMenu[] getOptions(UserData userData) {
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
        return getAutoSellInfo(user, fcd.getData());
    }


    private String getAutoSellInfo(UserData user, FcdParamsGetDto fcd) {
        var translations = botTexts.forUser(user, "parameters.autosell.UserAutoSell");
        String sellWorksStr = getSellWorksStr(fcd, translations);
        String evalModeStr = getEvalModeStr(fcd, translations);
        String followWorksStr = getFollowWorks(fcd, translations.inBundle("parameters.follow.AbstractPco"));

        return String.format("""
                        %s
                        
                        %s <b>%s</b>
                        <blockquote expandable>• %s
                        • %s: %s</blockquote>
                        
                        %s
                        %s
                        
                        %s
                        """,
                        fcd.getProfileStr(user, texts.forUser(user, "summary.ParamsSummary")),
                        DynamicEmoji.ITEM_SEND.getEmoji(),
                        translations.get("selling-parameters"),
                        translations.format("return-from-to", Map.of("value1", String.format("<b>%.2f%%</b>", fcd.getMinSellProfit() * 100), "value2", String.format("<b>%.2f%%</b>", fcd.getMaxSellProfit() * 100))),
                        translations.get("valuation-mode"),
                        evalModeStr,
                        followWorksStr,
                        sellWorksStr,
                        fcd.getDirection());
    }

    private String getSellWorksStr(FcdParamsGetDto tdp, LocalizedTexts translations) {
        return getWorksStr(tdp.getSellWorks(), translations);
    }

    private String getWorksStr(boolean b, LocalizedTexts translations) {
        return b
                ? String.format("%s <b>%s</b>",
                        DynamicEmoji.ON.getEmoji(),
                        translations.get("selling-is-active"))
                : String.format("%s <b>%s</b>",
                        DynamicEmoji.OFF.getEmoji(),
                        translations.get("selling-is-inactive"));
    }

    private String getEvalModeStr(FcdParamsGetDto fcd, LocalizedTexts translations) {
        SellPriceEvalMode mode = fcd.getEvalMode();
        Integer suggEvalModeC1 = fcd.getSuggEvalModeC1();

        if (mode == null) return "—";
        return switch (mode) {
            case DEFAULT -> String.format("<b>%s</b>",
                    translations.get("standard"));
            case INTELLIGENT_V1 -> {
                int sugg = (suggEvalModeC1 != null) ? suggEvalModeC1 : 50;
                yield String.format("""
                                <b>Intelligent v1</b>
                                • %s
                                
                                <b>%s</b>""",
                                translations.format("evalmodec1-parameter-recommended", Map.of("value1", String.format("<b>%d</b>", fcd.getEvalModeC1()), "value2", String.format("<b>%d</b>", sugg))),
                                evalModeS1WorksStr(fcd, translations));
            }
        };
    }

    private String evalModeS1WorksStr(FcdParamsGetDto fcd, LocalizedTexts translations) {
        return fcd.getEvalModeS1()
                ? String.format("✅ %s",
                        translations.get("evalmodes1-is-active"))
                : String.format("❌ %s",
                        translations.get("evalmodes1-is-inactive"));
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
        return !fcd.getQcData().isExists();
    }
}
