package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.api;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractAddApiState extends YTPTextMenuState<TokenAddValueMenu> {
    private final UserApiRegistry registry;

    public AbstractAddApiState(
            UserTextMessageSender sender,
            UserApiRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var data = registry.getOrCreate(userData, UserApiData::new);
        String marketName = getMarketType(data).getMarketName();
        return String.format("""
                        <blockquote>%s <b>ВНИМАНИЕ! API-ключ даёт полный доступ</b> к вашему аккаунту. <b>Никому его не сообщайте!</b> Бот никогда не запросит ключ повторно.</blockquote>
                        
                        %s Теперь <b>скопируйте API-ключ</b> со страницы <a href="%s"><b>%s</b></a> и <b>отправьте</b> его <b>в этот чат</b>""",
                DynamicEmoji.WARNING.getEmoji(),
                DynamicEmoji.COPY_2.getEmoji(),
                decideAPI(userData),
                marketName
        );
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        Supplier<Integer> mesIdSupplier = () -> update.getMessage().getMessageId();
        sender.deleteMes(bot, userData, mesIdSupplier, null);

        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.USER;
        }

        String token = update.getMessage().getText();
        var data = registry.getOrCreate(userData, UserApiData::new);
        data.setApi(token);
        return getNextState(bot, update, userData);
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
            case GET_API, RETURN -> UserMenu.ACCOUNTS;
        };
    }

    @Override
    public Map<TokenAddValueMenu, String> getUrls(UserData userData) {
        return Map.of(
                TokenAddValueMenu.GET_API, decideAPI(userData)
        );
    }

    private String decideAPI(UserData userData) {
        var data = registry.getOrCreate(userData, UserApiData::new);
        return getAPIPage(getMarketType(data));
    }

    private String getAPIPage(MarketType type) {
        return switch (type) {
            case CSFLOAT -> "https://csfloat.com/profile";
            case LIS_SKINS -> "https://lis-skins.com/ru/profile/api/";
            case MARKET_CSGO ->
                    "https://market.csgo.com/usercab/settings/security?utm_campaign=free&utm_source=youtradecs&utm_medium=telegram&cpid=21df92e0-95f3-4371-a6e3-bc20b9419289&oid=4c69d079-ad2a-44b0-a9ac-d0afc2167ee7";
            default -> "";
        };
    }

    public abstract UserMenu getNextState(TelegramClient bot, Update update, UserData userData);

    public abstract MarketType getMarketType(UserApiData apiData);
}
