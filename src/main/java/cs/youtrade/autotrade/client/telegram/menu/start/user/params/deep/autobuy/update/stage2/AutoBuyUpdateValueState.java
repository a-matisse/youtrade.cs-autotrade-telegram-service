package cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.autobuy.update.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.autobuy.update.UserAutoBuyUpdateData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.autobuy.update.UserAutoBuyUpdateRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class AutoBuyUpdateValueState extends AbstractTextState {
    private final UserAutoBuyUpdateRegistry registry;

    public AutoBuyUpdateValueState(
            UserTextMessageSender sender,
            UserAutoBuyUpdateRegistry registry
    ) {

        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        var data = registry.getOrCreate(user, UserAutoBuyUpdateData::new);
        return data.getField().getForkByField();
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.AUTOBUY_UPDATE_FIELD_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.AUTOBUY;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(user, UserAutoBuyUpdateData::new);
        data.setValue(value);
        return UserMenu.AUTOBUY_UPDATE_FIELD_STAGE_P;
    }
}
