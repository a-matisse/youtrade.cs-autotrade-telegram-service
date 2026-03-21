package cs.youtrade.autotrade.client.telegram.menu.start.user.token.rename.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.rename.UserRenameData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.rename.UserTokenRenameRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdTokenGetSingleDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class TokenRenameIdState extends YTPTextState {
    private final UserTokenRenameRegistry registry;
    private final GeneralEndpoint endpoint;

    public TokenRenameIdState(
            UserTextMessageSender sender,
            UserTokenRenameRegistry registry,
            GeneralEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        return String.format("""                        
                        📋 <b>Выбор токена для переименования</b>
                        ━━━━━━━━━━━━━━━━━━━━
                        <blockquote expandable>%s</blockquote>
                        Пожалуйста, <b>введите token-ID для смены имени</b>...
                        """,
                getStr(userData)
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_RENAME_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.TOKEN;
        }

        String input = update.getMessage().getText();
        long tokenId;
        try {
            tokenId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: Введенное значение не является числом: %s", input));
            return UserMenu.TOKEN;
        }

        var data = registry.getOrCreate(user, UserRenameData::new);
        data.setId(tokenId);
        return UserMenu.TOKEN_RENAME_STAGE_2;
    }

    private String getStr(UserData user) {
        var restAns = endpoint.getTokens(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var data = fcd.getData();
        if (data.isEmpty())
            return "⛔ Список токенов пуст\n";

        return fcd
                .getData()
                .stream()
                .map(FcdTokenGetSingleDto::asMessage)
                .collect(Collectors.joining("\n"));
    }
}
