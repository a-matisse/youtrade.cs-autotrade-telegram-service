package cs.youtrade.autotrade.client.telegram.menu.start.user.params;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.buy.BuyEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.function.Predicate;

@Service
public class UserDeepParamsState extends YTPTextMenuState<UserDeepParamsMenu> {
    private final ParamsEndpoint paramsEndpoint;
    private final BuyEndpoint buyEndpoint;

    public UserDeepParamsState(
            UserTextMessageSender sender,
            ParamsEndpoint paramsEndpoint,
            BuyEndpoint buyEndpoint
    ) {
        super(sender);
        this.paramsEndpoint = paramsEndpoint;
        this.buyEndpoint = buyEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PARAMS;
    }

    @Override
    public UserDeepParamsMenu getOption(String optionStr) {
        return UserDeepParamsMenu.valueOf(optionStr);
    }

    @Override
    public UserDeepParamsMenu[] getOptions(UserData userData) {
        return UserDeepParamsMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserDeepParamsMenu t) {
        return switch (t) {
            case MARKET_MODE_MARKET, MARKET_MODE_BARGAIN -> {
                var ans = buyEndpoint.toggle(userData.getChatId());
                // Нужно уведомить пользователя, если невозможно
                if (ans.getStatus() < 300 && !ans.getResponse().isResult())
                    sender.sendTextMes(bot, userData, ans.getResponse().getCause());
                // Работаем с текущим меню
                yield UserMenu.PARAMS;
            }
            case PARAMS_TO_AUTOBUY -> UserMenu.AUTOBUY;
            case PARAMS_TO_AUTOSELL -> UserMenu.AUTOSELL;
            case PARAMS_RENAME -> UserMenu.PARAMS_RENAME_STAGE_1;
            case PARAMS_LIST -> UserMenu.PARAMS_LIST;
            case PARAMS_TO_FOLLOW -> UserMenu.FOLLOW;
            case PARAMS_CREATE -> UserMenu.PARAMS_CREATE_STAGE_1;
            case PARAMS_DELETE -> UserMenu.PARAMS_DELETE_STAGE_1;
            case RETURN -> UserMenu.START;
            case TO_QUICK_CONFIG -> UserMenu.USER;
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

        return String.format("""
                        %s <i>Углублённые параметры</i>
                        
                        %s
                        
                        %s
                        """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                fcd.getData().getProfileStr(userData),
                fcd.getData().getQcStr()
        );
    }

    @Override
    public Map<UserDeepParamsMenu, Predicate<UserData>> getVisibilityPredicates(UserData userData) {
        var restAns = paramsEndpoint.getCurrent(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return Map.of();

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return Map.of();

        var ans = fcd.getData();
        return Map.of(
                UserDeepParamsMenu.MARKET_MODE_MARKET, u -> isBargainAllowed(u, ans),
                UserDeepParamsMenu.MARKET_MODE_BARGAIN, u -> isBargainAllowed(u, ans),
                UserDeepParamsMenu.PARAMS_TO_AUTOBUY, UserData::isQualified,
                UserDeepParamsMenu.PARAMS_TO_AUTOSELL, UserData::isQualified,
                UserDeepParamsMenu.PARAMS_TO_FOLLOW, UserData::isQualified
        );
    }

    public boolean isBargainAllowed(UserData userData, FcdParamsGetDto params) {
        return userData.isBargainAllowed() && params.getSource().isBargainable();
    }
}
