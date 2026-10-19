package cs.youtrade.autotrade.client.telegram.menu.start.user;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.preferences.UserPreferencesState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.BuyEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Service
public class UserParamsState extends YTPTextMenuState<UserParamsMenu> {
    private final Map<UserData, FcdParamsGetDto> paramsData = new ConcurrentHashMap<>();
    private final ParamsEndpoint paramsEndpoint;
    private final BuyEndpoint buyEndpoint;
    private final SellDefaultEndpoint sellEndpoint;
    private final UserPreferencesState preferencesState;
    private final BotTexts texts;

    public UserParamsState(
            UserTextMessageSender sender,
            ParamsEndpoint paramsEndpoint,
            BuyEndpoint buyEndpoint,
            SellDefaultEndpoint sellEndpoint,
            UserPreferencesState preferencesState,
            BotTexts texts
    ) {
        super(sender, texts);
        this.paramsEndpoint = paramsEndpoint;
        this.buyEndpoint = buyEndpoint;
        this.sellEndpoint = sellEndpoint;
        this.preferencesState = preferencesState;
        this.texts = texts;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.USER;
    }

    @Override
    public UserParamsMenu getOption(String optionStr) {
        return UserParamsMenu.valueOf(optionStr);
    }

    @Override
    public UserParamsMenu[] getOptions(UserData userData) {
        return UserParamsMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserParamsMenu t) {
        return switch (t) {
            case PARAMS_BUY_ON, PARAMS_BUY_OFF -> {
                var ans = buyEndpoint.toggle(userData.getChatId());
                // Нужно уведомить пользователя, если невозможно
                if (ans.getStatus() < 300 && !ans.getResponse().isResult())
                    sender.sendTextMes(bot, userData, ans.getResponse().getCause());
                // Работаем с текущим меню
                yield UserMenu.USER;
            }
            case PARAMS_SELL_ON, PARAMS_SELL_OFF -> {
                var ans = sellEndpoint.toggle(userData.getChatId());
                // Нужно уведомить пользователя, если невозможно
                if (ans.getStatus() < 300 && !ans.getResponse().isResult())
                    sender.sendTextMes(bot, userData, ans.getResponse().getCause());
                // Работаем с текущим меню
                yield UserMenu.USER;
            }
            case PARAMS_PORTFOLIO -> UserMenu.PORTFOLIO;
            case PARAMS_QUICK_ENABLE -> UserMenu.USER_QUICK_CONFIG_DISABLE;
            case PARAMS_QUICK_DISABLE -> UserMenu.USER_QUICK_CONFIG_INIT_STAGE_1;
            case PARAMS_TO_TOKENS -> UserMenu.ACCOUNTS;
            case PARAMS_SWITCH -> UserMenu.PARAMS_SWITCH_STAGE_1;
            case PREFERENCES -> {
                preferencesState.openFrom(userData, UserMenu.USER);
                yield UserMenu.PREFERENCES;
            }
            case RETURN -> UserMenu.START;
            case TO_DEEP_PARAMS -> UserMenu.PARAMS;
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
        return getParamsInfo(userData, fcd.getData());
    }

    private String getParamsInfo(UserData user, FcdParamsGetDto fcd) {
        var translations = botTexts.forUser(user, "parameters.UserParams");
        return String.format("""
                        %s <i>%s</i>
                        
                        %s
                        
                        %s <b>%s</b>
                        <blockquote>• %s → <tg-spoiler><b>$%.2f</b></tg-spoiler>
                        %s</blockquote>
                        
                        %s
                        
                        %s
                        """,
                        DynamicEmoji.YOUTRADE.getEmoji(),
                        translations.get("account-parameters"),
                        fcd.getProfileStr(user, texts.forUser(user, "summary.ParamsSummary")),
                        DynamicEmoji.MONEY.getEmoji(),
                        translations.get("finances"),
                        translations.get("y-cs-balance"),
                        fcd.getBalance(),
                        fcd.getVolumeStr(translations.inBundle("summary.ParamsSummary")),
                        fcd.getQcStr(translations.inBundle("summary.ParamsSummary")),
                        fcd.getDirection());
    }

    @Override
    public Map<UserParamsMenu, Predicate<UserData>> getVisibilityPredicates(UserData user) {
        return Map.of(
                UserParamsMenu.PARAMS_BUY_ON, this::buyOnButtonPredicate,
                UserParamsMenu.PARAMS_BUY_OFF, u -> !buyOnButtonPredicate(u),
                UserParamsMenu.PARAMS_SELL_ON, this::sellOnButtonPredicate,
                UserParamsMenu.PARAMS_SELL_OFF, u -> !sellOnButtonPredicate(u),
                UserParamsMenu.PARAMS_QUICK_ENABLE, this::quickConfigEnabled,
                UserParamsMenu.PARAMS_QUICK_DISABLE, u -> !quickConfigEnabled(u)
        );
    }

    private boolean buyOnButtonPredicate(UserData user) {
        var fcd = paramsData.get(user);
        return fcd.getBuyWorks();
    }

    private boolean sellOnButtonPredicate(UserData user) {
        var fcd = paramsData.get(user);
        return fcd.getSellWorks();
    }

    private boolean quickConfigEnabled(UserData user) {
        var fcd = paramsData.get(user);
        return fcd.getQcData().isExists();
    }
}
