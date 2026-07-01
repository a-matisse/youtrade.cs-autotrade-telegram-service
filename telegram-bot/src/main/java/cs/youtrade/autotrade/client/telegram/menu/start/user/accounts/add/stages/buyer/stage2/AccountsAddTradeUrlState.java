package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.buyer.stage2;

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
            UserApiRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_ADD_STAGE_2_BUYER;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        <blockquote>%s <b>Trade-ссылкой МОЖНО делиться</b> — она нужна для отправки вам трейдов. В отличие от API-ключа, <b>эта ссылка не даёт доступа</b> к управлению аккаунтом.</blockquote>
                        
                        %s Теперь <b>скопируйте <a href="%s">Trade-ссылку</a></b> со страницы и <b>отправьте</b> её <b>в этот чат</b>""",
                DynamicEmoji.SUCCESS.getEmoji(),
                DynamicEmoji.COPY_2.getEmoji(),
                getAPIPage()
        );
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.ACCOUNTS;
        }

        Supplier<Integer> mesIdSupplier = () -> update.getMessage().getMessageId();
        sender.deleteMes(bot, userData, mesIdSupplier, null);

        String tradeUrl = update.getMessage().getText();
        String partnerId = extractPartnerId(tradeUrl);
        String steamToken = extractToken(tradeUrl);
        if (partnerId == null) {
            sender.sendTextMes(bot, userData, "#1: некорректный формат trade URL.");
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
