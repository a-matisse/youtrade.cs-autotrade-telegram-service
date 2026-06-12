package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.UserTokenDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.UserTokenDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdTokenGetSingleDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class TokenDeleteIdState extends YTPTextState {
    private final UserTokenDeleteRegistry registry;
    private final GeneralEndpoint endpoint;

    public TokenDeleteIdState(
            UserTextMessageSender sender,
            UserTokenDeleteRegistry registry,
            GeneralEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        return String.format("""                        
                        %s <b>Пожалуйста, введите token-ID для удаления...</b>
                        
                        <blockquote expandable>%s</blockquote>
                        
                        %s <i><b>Осторожно!</b> При удалении будут утеряны все данные аккаунта</i>
                        """,
                DynamicEmoji.WRITE.getEmoji(),
                getStr(userData),
                DynamicEmoji.WARNING.getEmoji()
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_REMOVE_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.ACCOUNTS;
        }

        String input = update.getMessage().getText();
        long tokenId;
        try {
            tokenId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: Введенное значение не является числом: %s", input));
            return UserMenu.ACCOUNTS;
        }

        var data = registry.getOrCreate(user, UserTokenDeleteData::new);
        data.setTokenId(tokenId);
        return UserMenu.ACCOUNTS_REMOVE_STAGE_P;
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
            return String.format("%s Список аккаунтов пуст",
                    DynamicEmoji.ERROR.getEmoji());

        return fcd
                .getData()
                .stream()
                .map(FcdTokenGetSingleDto::asMessage)
                .collect(Collectors.joining("\n"))
                .trim();
    }
}
