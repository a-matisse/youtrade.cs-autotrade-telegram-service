package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.change.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.change.TableChangeRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellChangeEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableChangeProceedState extends AbstractTerminalTextMenuState {
    private final TableChangeRegistry registry;
    private final SellChangeEndpoint endpoint;

    public TableChangeProceedState(
            UserTextMessageSender sender,
            TableChangeRegistry registry,
            SellChangeEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TABLE_CHANGE_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        long chatId = user.getChatId();
        var data = registry.remove(user);
        var restAns = switch (data.getType()) {
            case SINGLE -> endpoint.postChanges(chatId, data.getDtos());
            case GROUPED -> endpoint.postChangesGroups(chatId, data.getDtos());
        };
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return fcd.getData()
                ? "Диапазоны цен успешно обновлены 👍"
                : "Ошибка обновления ❌";
    }

    @Override
    public UserMenu retState() {
        return UserMenu.TABLE;
    }
}
