package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.api;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
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
            UserApiRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.add.AbstractAddApi");
        var data = registry.getOrCreate(userData, UserApiData::new);
        String marketName = getMarketType(data).getMarketName();
        return String.format("""
                        <blockquote>%s %s</blockquote>
                        
                        %s %s""",
                        DynamicEmoji.WARNING.getEmoji(),
                        translations.format("warning-an-api-key-gives-full-access", Map.of("value1", String.format("<b>%s</b>", translations.get("do-not-share-it-with-anyone")), "emphasis", String.format("<b>%s</b>", translations.get("warning-an-api-key-gives-full-access.emphasis")))),
                        DynamicEmoji.COPY_2.getEmoji(),
                        translations.format("now-from-the-page-and-it", Map.of("value1", String.format("<b>%s</b>", translations.get("copy-the-api-key")), "value2", String.format("<a href=\"%s\"><b>%s</b></a>", decideAPI(userData), marketName), "value3", String.format("<b>%s</b>", translations.get("send")), "value4", String.format("<b>%s</b>", translations.get("in-this-chat")))));
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.add.AbstractAddApi");
        Supplier<Integer> mesIdSupplier = () -> update.getMessage().getMessageId();
        sender.deleteMes(bot, userData, mesIdSupplier, null);

        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
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
