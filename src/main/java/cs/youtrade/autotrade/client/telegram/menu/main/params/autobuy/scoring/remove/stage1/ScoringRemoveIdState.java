package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.remove.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.remove.ScoringRemoveData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.remove.ScoringRemoveRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class ScoringRemoveIdState extends AbstractTextState {
    private final ScoringRemoveRegistry registry;

    public ScoringRemoveIdState(
            UserTextMessageSender sender,
            ScoringRemoveRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage() {
        return "Пожалуйста, введите scoring-ID (целое число)...";
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_REMOVE_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.SCORING;
        }

        String input = update.getMessage().getText();
        long profitId;
        try {
            profitId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, chatId, String.format("#1: Введенное значение не является числом: %s", input));
            return UserMenu.SCORING;
        }

        var data = registry.getOrCreate(user, ScoringRemoveData::new);
        data.setProfitId(profitId);
        return null;
    }
}
