package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.TableHistoryData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.TableHistoryMode;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.TableHistoryRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableHistoryModeState extends AbstractTextMenuState<TableHistoryMode> {
    private final TableHistoryRegistry registry;

    public TableHistoryModeState(
            UserTextMessageSender sender,
            TableHistoryRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_HISTORY_STAGE_CHOOSE;
    }

    @Override
    public TableHistoryMode getOption(String optionStr) {
        return TableHistoryMode.valueOf(optionStr);
    }

    @Override
    public TableHistoryMode[] getOptions() {
        return TableHistoryMode.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, TableHistoryMode t) {
        if (t.equals(TableHistoryMode.RETURN))
            return UserMenu.PORTFOLIO;

        var data = registry.getOrCreate(user, TableHistoryData::new);
        data.setMode(t);
        return UserMenu.PORTFOLIO_HISTORY_STAGE_1;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return """
                <b>🗄 Выбор типа истории сделок</b>
                ━━━━━━━━━━━━━━━━
                <blockquote>📥 <b>Покупка</b> — покупки <b>без статистики дохода</b>
                📤 <b>Продажа</b> — покупки <b>со статистикой дохода</b></blockquote>
                
                <b>Выберите вариант ниже...</b>
                """;
    }
}
