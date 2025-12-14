package cs.youtrade.autotrade.client.telegram.menu.start.user.params.token.add.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.token.add.UserTokenAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.token.add.UserTokenAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@Log4j2
public class TokenAddTradeUrlState extends AbstractTextState {
    private final UserTokenAddRegistry registry;

    public TokenAddTradeUrlState(
            UserTextMessageSender sender,
            UserTokenAddRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        return """
                Теперь введите ссылку на обмен, привязанную к этому аккаунту...
                """;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_ADD_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.TOKEN;
        }

        String tradeUrl = update.getMessage().getText();
        String partnerId = extractPartnerId(tradeUrl);
        String steamToken = extractToken(tradeUrl);
        if (partnerId == null) {
            sender.sendTextMes(bot, chatId, "#1: некорректный формат trade URL.");
            return UserMenu.TOKEN;
        }

        var data = registry.getOrCreate(user, UserTokenAddData::new);
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
}
