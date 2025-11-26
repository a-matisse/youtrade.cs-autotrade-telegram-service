package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.history.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.history.TableHistoryData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.history.TableHistoryRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableHistoryPeriodState extends AbstractTextState {
    private final TableHistoryRegistry registry;

    public TableHistoryPeriodState(
            UserTextMessageSender sender,
            TableHistoryRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage() {
        return "Пожалуйста, введите период отсчета истории (в днях)...";
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TABLE_CHANGE_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.TABLE;
        }

        String input = update.getMessage().getText();
        int days;
        try {
            days = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, chatId, String.format("#1: Введенное значение не является натуральным числом: %s", input));
            return UserMenu.TABLE;
        }

        var data = registry.getOrCreate(user, TableHistoryData::new);
        data.setPeriod(days);
        return UserMenu.TABLE_HISTORY_STAGE_P;
    }
}
