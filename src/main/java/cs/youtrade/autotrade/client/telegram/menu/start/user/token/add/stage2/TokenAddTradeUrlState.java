package cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.UserTokenAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.UserTokenAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Service
@Log4j2
public class TokenAddTradeUrlState extends YTPTextMenuState<TokenAddTradeUrlMenu> {
    private final UserTokenAddRegistry registry;

    public TokenAddTradeUrlState(
            UserTextMessageSender sender,
            UserTokenAddRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_ADD_STAGE_2;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        Теперь <b>скопируйте <a href="%s">Trade-ссылку</a></b> со страницы и <b>отправьте</b> её <b>в этот чат</b>
                        
                        <blockquote>✅ <b>Trade-ссылкой МОЖНО делиться</b> — она нужна для отправки вам трейдов. В отличие от API-ключа, эта ссылка не даёт доступа к управлению аккаунтом.</blockquote>
                        """,
                getAPIPage()
        );
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.TOKEN;
        }

        int mesId = update.getMessage().getMessageId();
        sender.deleteMes(bot, userData, mesId, null);

        String tradeUrl = update.getMessage().getText();
        String partnerId = extractPartnerId(tradeUrl);
        String steamToken = extractToken(tradeUrl);
        if (partnerId == null) {
            sender.sendTextMes(bot, userData, "#1: некорректный формат trade URL.");
            return UserMenu.TOKEN;
        }

        var data = registry.getOrCreate(userData, UserTokenAddData::new);
        data.setPartnerId(partnerId);
        data.setSteamToken(steamToken);
        return UserMenu.TOKEN_ADD_STAGE_P;
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
    public TokenAddTradeUrlMenu getOption(String optionStr) {
        return TokenAddTradeUrlMenu.valueOf(optionStr);
    }

    @Override
    public TokenAddTradeUrlMenu[] getOptions(UserData userData) {
        return TokenAddTradeUrlMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, TokenAddTradeUrlMenu t) {
        return switch (t) {
            case GET_TRADE, RETURN -> UserMenu.TOKEN;
        };
    }

    @Override
    public Map<TokenAddTradeUrlMenu, String> getUrls(UserData userData) {
        return Map.of(
                TokenAddTradeUrlMenu.GET_TRADE, getAPIPage()
        );
    }

    private String getAPIPage() {
        return "https://steamcommunity.com/id/me/tradeoffers/privacy";
    }
}
