package cs.youtrade.autotrade.client.telegram.menu.main.params.token.delete.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.token.delete.UserTokenDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.token.delete.UserTokenDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdTokenGetSingleDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class TokenDeleteIdState extends AbstractTextState {
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
    protected String getMessage(UserData user) {
        return String.format("""
                        Пожалуйста, введите token-ID для удаления...
                        (Осторожно! При удалении будут утеряны все данные токена)
                        
                        Список ваших token-ID:
                        %s
                        """,
                getStr(user)
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_REMOVE_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.PARAMS;
        }

        String input = update.getMessage().getText();
        long tokenId;
        try {
            tokenId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, chatId, String.format("#1: Введенное значение не является числом: %s", input));
            return UserMenu.PARAMS;
        }

        var data = registry.getOrCreate(user, UserTokenDeleteData::new);
        data.setTokenId(tokenId);
        return UserMenu.WORDS_REMOVE_STAGE_P;
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
            return "Список token-ID пуст...";

        return fcd
                .getData()
                .stream()
                .map(FcdTokenGetSingleDto::asMessage)
                .collect(Collectors.joining("\n\n"));
    }
}
