package cs.youtrade.autotrade.client.telegram.menu.start.user.params;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Service
public class UserParamsState extends AbstractTextMenuState<UserParamsMenu> {
    private final Map<UserData, FcdParamsGetDto> paramsData = new ConcurrentHashMap<>();
    private final ParamsEndpoint paramsEndpoint;

    public UserParamsState(
            UserTextMessageSender sender,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS;
    }

    @Override
    public UserParamsMenu getOption(String optionStr) {
        return UserParamsMenu.valueOf(optionStr);
    }

    @Override
    public UserParamsMenu[] getOptions() {
        return UserParamsMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserParamsMenu t) {
        return switch (t) {
            case PARAMS_BUY_ON, PARAMS_BUY_OFF -> UserMenu.PARAMS_TOGGLE_AUTOBUY;
            case PARAMS_SELL_ON, PARAMS_SELL_OFF -> UserMenu.PARAMS_TOGGLE_AUTOSELL;
            case PARAMS_PORTFOLIO -> UserMenu.PORTFOLIO;
            case PARAMS_QUICK_ENABLE -> UserMenu.PARAMS_QUICK_CONFIG_DISABLE;
            case PARAMS_QUICK_DISABLE -> UserMenu.PARAMS_QUICK_CONFIG_INIT_STAGE_1;
            case PARAMS_TO_TOKENS -> UserMenu.TOKEN;
            case PARAMS_ADVANCED_SETTINGS -> UserMenu.DEEP_PARAMS;
            case PARAMS_TO_FOLLOW -> UserMenu.FOLLOW;
            case RETURN -> UserMenu.USER;
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
        return getParamsInfo(fcd.getData());
    }

    private String getParamsInfo(FcdParamsGetDto fcd) {
        return String.format("""
                        ⚙️ <b>Параметры аккаунта</b>
                        ━━━━━━━━━━━━━━━━━━━━━
                        
                        👤 Профиль: <b>%s</b>
                        🆔 Params ID: <b>%s</b>
                        %s
                        
                        <b>%s</b> → <b>%s</b>
                        """,
                fcd.getGivenName(),
                fcd.getTdpId(),
                fcd.getVolumeStr(),
                fcd.getSource().getMarketName(),
                fcd.getDestination().getMarketName()
        );
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
        return fcd.getConfigExists();
    }
}
