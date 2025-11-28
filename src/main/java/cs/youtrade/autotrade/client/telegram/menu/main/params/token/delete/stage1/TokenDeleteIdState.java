package cs.youtrade.autotrade.client.telegram.menu.main.params.token.delete.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.token.delete.UserTokenDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.token.delete.UserTokenDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TokenDeleteIdState extends AbstractTextState {
    private final UserTokenDeleteRegistry registry;

    public TokenDeleteIdState(
            UserTextMessageSender sender,
            UserTokenDeleteRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        return """
                Пожалуйста, введите token-ID для удаления...
                (Осторожно! При удалении будут утеряны все данные токена)
                """;
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
}
