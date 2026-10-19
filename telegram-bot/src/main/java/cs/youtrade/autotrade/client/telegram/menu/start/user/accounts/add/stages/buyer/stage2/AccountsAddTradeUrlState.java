package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.buyer.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Supplier;

@Service
@Log4j2
public class AccountsAddTradeUrlState extends YTPTextMenuState<AccountsAddTradeUrlMenu> {
    private final UserApiRegistry registry;

    public AccountsAddTradeUrlState(
            UserTextMessageSender sender,
            UserApiRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_ADD_STAGE_2_BUYER;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.add.BuyerAdd");
        return String.format("""
                        <blockquote>%s %s</blockquote>
                        
                        %s %s""",
                        DynamicEmoji.SUCCESS.getEmoji(),
                        translations.format("your-trade-url-can-be-shared-b", Map.of("value1", String.format("<b>%s</b>", translations.get("this-url-does-not-give-access")), "emphasis", String.format("<b>%s</b>", translations.get("your-trade-url-can-be-shared-b.emphasis")))),
                        DynamicEmoji.COPY_2.getEmoji(),
                        translations.format("now-from-the-page-and-it", Map.of("value1", String.format("<b>%s</b>", translations.format("copy-the", Map.of("value1", String.format("<a href=\"%s\">%s</a>", getAPIPage(), translations.get("trade-url"))))), "value2", String.format("<b>%s</b>", translations.get("send")), "value3", String.format("<b>%s</b>", translations.get("in-this-chat")))));
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.add.BuyerAdd");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.ACCOUNTS;
        }

        Supplier<Integer> mesIdSupplier = () -> update.getMessage().getMessageId();
        sender.deleteMes(bot, userData, mesIdSupplier, null);

        String tradeUrl = update.getMessage().getText();
        String partnerId = extractPartnerId(tradeUrl);
        String steamToken = extractToken(tradeUrl);
        if (partnerId == null) {
            sender.sendTextMes(bot, userData, String.format("#1: %s",
                    translations.get("invalid-trade-url-format")));
            return UserMenu.ACCOUNTS;
        }

        var data = registry.getOrCreate(userData, UserApiData::new);
        data.setPartnerId(partnerId);
        data.setSteamToken(steamToken);
        return UserMenu.ACCOUNTS_ADD_STAGE_P_BUYER;
    }

    private String extractPartnerId(String tradeUrl) {
        try {
            URI uri = new URI(tradeUrl);
            String query = uri.getQuery();
            if (query == null)
                return null;

            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "partner".equals(pair[0]))
                    return pair[1];
            }
        } catch (URISyntaxException e) {
            log.error("Ошибка разбора trade URL: " + e.getMessage());
        }
        return null;
    }

    private String extractToken(String tradeUrl) {
        try {
            URI uri = new URI(tradeUrl);
            String query = uri.getQuery();
            if (query == null)
                return null;

            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "token".equals(pair[0]))
                    return pair[1];
            }
        } catch (URISyntaxException e) {
            log.error("Ошибка разбора trade URL: " + e.getMessage());
        }
        return null;
    }

    @Override
    public AccountsAddTradeUrlMenu getOption(String optionStr) {
        return AccountsAddTradeUrlMenu.valueOf(optionStr);
    }

    @Override
    public AccountsAddTradeUrlMenu[] getOptions(UserData userData) {
        return AccountsAddTradeUrlMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, AccountsAddTradeUrlMenu t) {
        return switch (t) {
            case GET_TRADE, RETURN -> UserMenu.ACCOUNTS;
        };
    }

    @Override
    public Map<AccountsAddTradeUrlMenu, String> getUrls(UserData userData) {
        return Map.of(
                AccountsAddTradeUrlMenu.GET_TRADE, getAPIPage()
        );
    }

    private String getAPIPage() {
        return "https://steamcommunity.com/id/me/tradeoffers/privacy";
    }
}
