package cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.UserTokenAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.UserTokenAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;

@Service
public class TokenAddValueState extends YTPTextMenuState<TokenAddValueMenu> {
    private final UserTokenAddRegistry registry;

    public TokenAddValueState(
            UserTextMessageSender sender,
            UserTokenAddRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var data = registry.getOrCreate(userData, UserTokenAddData::new);
        String marketName = switch (data.getOpt()) {
            case BUY_TOKEN -> data.getSource().getMarketName();
            case SELL_TOKEN -> data.getDestination().getMarketName();
            default -> "";
        };
        return String.format("""
                        Теперь <b>скопируйте API-ключ</b> со страницы <a href="%s"><b>%s</b></a> и <b>отправьте</b> его <b>в этот чат</b>
                        
                        <blockquote>⚠️ <b>ВНИМАНИЕ!</b> API-ключ даёт полный доступ к вашему аккаунту. <b>Никому его не сообщайте!</b> Бот никогда не запросит ключ повторно.</blockquote>""",
                decideAPI(userData), marketName
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_ADD_STAGE_1;
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        int mesId = update.getMessage().getMessageId();
        sender.deleteMes(bot, userData, mesId, null);

        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.USER;
        }

        String token = update.getMessage().getText();
        var data = registry.getOrCreate(userData, UserTokenAddData::new);
        data.setApi(token);
        return switch (data.getOpt()) {
            case BUY_TOKEN -> UserMenu.TOKEN_ADD_STAGE_2;
            case SELL_TOKEN -> UserMenu.TOKEN_ADD_STAGE_P;
            case RETURN -> UserMenu.TOKEN;
        };
    }

    @Override
    public TokenAddValueMenu getOption(String optionStr) {
        return TokenAddValueMenu.valueOf(optionStr);
    }

    @Override
    public TokenAddValueMenu[] getOptions(UserData userData) {
        return TokenAddValueMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, TokenAddValueMenu t) {
        return switch (t) {
            case GET_API, RETURN -> UserMenu.TOKEN;
        };
    }

    @Override
    public Map<TokenAddValueMenu, String> getUrls(UserData userData) {
        return Map.of(
                TokenAddValueMenu.GET_API, decideAPI(userData)
        );
    }

    private String decideAPI(UserData userData) {
        var data = registry.getOrCreate(userData, UserTokenAddData::new);
        return switch (data.getOpt()) {
            case BUY_TOKEN -> getAPIPage(data.getSource());
            case SELL_TOKEN -> getAPIPage(data.getDestination());
            default -> "";
        };
    }

    private String getAPIPage(MarketType type) {
        return switch (type) {
            case CSFLOAT -> "https://csfloat.com/profile";
            case LIS_SKINS -> "https://lis-skins.com/ru/profile/api/";
            case MARKET_CSGO -> "https://market.csgo.com/usercab/settings/security";
            default -> "";
        };
    }
}
