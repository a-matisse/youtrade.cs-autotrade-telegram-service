package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.edit.stage3;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.edit.ScoringEditData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.edit.ScoringEditRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ScoringEditValueState extends AbstractTextState {
    private final ScoringEditRegistry registry;

    public ScoringEditValueState(
            UserTextMessageSender sender,
            ScoringEditRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        var data = registry.getOrCreate(user, ScoringEditData::new);
        return data.getField().getForkByField();
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_EDIT_STAGE_3;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.SCORING;
        }

        String value = update.getMessage().getText();
        var data = registry.getOrCreate(user, ScoringEditData::new);
        data.setValue(value);
        return UserMenu.SCORING_EDIT_STAGE_P;
    }
}
