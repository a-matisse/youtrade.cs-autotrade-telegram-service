package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.TableChangeData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.TableChangeRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.TableChangeType;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableChangeChooseState extends YTPTextMenuState<TableChangeType> {
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
    public TableChangeType[] getOptions(UserData userData) {
        return TableChangeType.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, TableChangeType t) {
        if (t.equals(TableChangeType.RETURN))
            return UserMenu.PORTFOLIO;

        var data = registry.getOrCreate(user, TableChangeData::new);
        data.setType(t);
        return UserMenu.PORTFOLIO_CHANGE_STAGE_1;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                %s <b>Выберите тип изменений в таблице</b>
                
                <blockquote>%s <b>Одиночные</b> изменения - работа с отдельными записями
                %s <b>Групповые</b> изменения - массовое редактирование данных</blockquote>
                """,
                DynamicEmoji.CHOOSE.getEmoji(),
                DynamicEmoji.NOTE.getEmoji(),
                DynamicEmoji.GRAPH.getEmoji()
        );
    }
}
