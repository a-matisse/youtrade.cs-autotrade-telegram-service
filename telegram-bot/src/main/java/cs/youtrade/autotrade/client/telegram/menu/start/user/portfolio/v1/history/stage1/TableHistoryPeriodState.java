package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.TableHistoryData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.TableHistoryMode;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.TableHistoryRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableHistoryPeriodState extends YTPTextMenuState<TableHistoryPeriodMenu> {
    private final TableHistoryRegistry registry;

    public TableHistoryPeriodState(
            UserTextMessageSender sender,
            TableHistoryRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var data = registry.getOrCreate(userData, TableHistoryData::new);
        boolean sellHistory = data.getMode() == TableHistoryMode.SELL;
        String title = sellHistory ? "История продаж" : "История покупок";
        String description = sellHistory
                ? "<b>Проданные предметы</b> за выбранный период будут собраны в таблице."
                : "<b>Купленные предметы</b> за выбранный период будут собраны в таблице.";
        String result = sellHistory
                ? "В итогах — объём, заработок и <b>фактическая доходность</b>."
                : "В итогах — количество и <b>общий объём покупок</b>.";
        String directionEmoji = sellHistory
                ? DynamicEmoji.ITEM_SEND.getEmoji()
                : DynamicEmoji.ITEM_RECEIVE.getEmoji();

        return String.format("""
                        %s <b>%s</b>

                        <blockquote>%s %s
                        %s %s</blockquote>

                        %s <b>Выберите период ниже или отправьте своё количество дней</b>
                        """,
                DynamicEmoji.EXCEL.getEmoji(), title,
                directionEmoji, description,
                DynamicEmoji.GRAPH.getEmoji(), result,
                DynamicEmoji.WRITE.getEmoji()
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_HISTORY_STAGE_1;
    }

    @Override
    public TableHistoryPeriodMenu getOption(String optionStr) {
        return TableHistoryPeriodMenu.valueOf(optionStr);
    }

    @Override
    public TableHistoryPeriodMenu[] getOptions(UserData userData) {
        return TableHistoryPeriodMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, TableHistoryPeriodMenu option) {
        return prepare(user, option.getDays());
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
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

        return prepare(user, days);
    }

    private UserMenu prepare(UserData user, int days) {
        var data = registry.getOrCreate(user, TableHistoryData::new);
        data.setPeriod(days);
        return switch (data.getMode()) {
            case SELL -> UserMenu.PORTFOLIO_HISTORY_STAGE_P_SELL;
            case BUY -> UserMenu.PORTFOLIO_HISTORY_STAGE_P_BUY;
            case RETURN -> throw new IllegalStateException("Cannot process RETURN state");
        };
    }
}
