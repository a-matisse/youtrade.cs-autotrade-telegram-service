package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.edit.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.edit.ScoringEditData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.edit.ScoringEditRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.TdpField;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ScoringEditFieldState extends AbstractTextState {
    private final ScoringEditRegistry registry;

    public ScoringEditFieldState(
            UserTextMessageSender sender,
            ScoringEditRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(UserData user) {
        return TdpField.generateDescription(TdpField.DirType.SCORING);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_EDIT_STAGE_2;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.SCORING;
        }

        String field = update.getMessage().getText();
        var data = registry.getOrCreate(user, ScoringEditData::new);
        TdpField tdpF = TdpField.fromFName(field);
        if (tdpF == null) {
            sender.sendTextMes(bot, chatId, "#0: Поле не найдено. Возвращение обратно...");
            return UserMenu.SCORING;
        }

        data.setField(tdpF);
        return UserMenu.SCORING_EDIT_STAGE_3;
    }
}
