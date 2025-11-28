package cs.youtrade.autotrade.client.telegram.menu.main.params.token.add.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.token.add.UserTokenAddData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.token.add.UserTokenAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TokenAddValueState extends AbstractTextState {
    private final UserTokenAddRegistry registry;

    public TokenAddValueState(
            UserTextMessageSender sender,
            UserTokenAddRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        return "Теперь введите токен...";
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_ADD_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.PARAMS;
        }

        String token = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserTokenAddData::new);
        data.setApi(token);
        return switch (data.getOpt()) {
            case BUY_TOKEN -> UserMenu.TOKEN_ADD_STAGE_2;
            case SELL_TOKEN -> UserMenu.TOKEN_ADD_STAGE_P;
        };
    }
}
