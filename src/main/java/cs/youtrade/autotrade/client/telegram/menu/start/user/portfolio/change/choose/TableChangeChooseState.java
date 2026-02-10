package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.TableChangeData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.TableChangeRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.TableChangeType;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableChangeChooseState extends AbstractTextMenuState<TableChangeType> {
    private final TableChangeRegistry registry;

    public TableChangeChooseState(
            UserTextMessageSender sender,
            TableChangeRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_CHANGE_STAGE_CHOOSE;
    }

    @Override
    public TableChangeType getOption(String optionStr) {
        return TableChangeType.valueOf(optionStr);
    }

    @Override
    public TableChangeType[] getOptions() {
        return TableChangeType.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, TableChangeType t) {
        var data = registry.getOrCreate(user, TableChangeData::new);
        data.setType(t);
        return UserMenu.PORTFOLIO_CHANGE_STAGE_1;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return """
                📊 <b>Выбор типа изменений в таблице</b>
                ━━━━━━━━━━━━━━━━━━
                <blockquote>• 📝 Одиночные изменения - работа с отдельными записями
                • 📊 Групповые изменения - массовое редактирование данных</blockquote>
                """;
    }
}
