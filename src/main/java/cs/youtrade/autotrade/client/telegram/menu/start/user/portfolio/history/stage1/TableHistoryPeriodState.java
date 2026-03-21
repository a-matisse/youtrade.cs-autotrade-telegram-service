package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.TableHistoryData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.TableHistoryRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableHistoryPeriodState extends YTPTextState {
    private final TableHistoryRegistry registry;

    public TableHistoryPeriodState(
            UserTextMessageSender sender,
            TableHistoryRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage(TelegramClient bot, UserData userData) {
        return """
                📋 <b>Укажите период</b>
                ━━━━━━━━━━
                
                Введите <b>количество дней</b> для загрузки истории
                └ <b>Пример</b>: <code>3</code> <i>дн.</i> • <code>7</code> <i>дн.</i> • <code>14</code> <i>дн.</i> • <code>30</code> <i>дн.</i>
                """;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_HISTORY_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.PORTFOLIO;
        }

        String input = update.getMessage().getText();
        int days;
        try {
            days = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            sender.sendTextMes(bot, user, String.format("#1: Введенное значение не является натуральным числом: %s", input));
            return UserMenu.PORTFOLIO;
        }

        var data = registry.getOrCreate(user, TableHistoryData::new);
        data.setPeriod(days);
        return switch (data.getMode()) {
            case SELL -> UserMenu.PORTFOLIO_HISTORY_STAGE_P_SELL;
            case BUY -> UserMenu.PORTFOLIO_HISTORY_STAGE_P_BUY;
            case RETURN -> throw new IllegalStateException("Cannot process RETURN state");
        };
    }
}
