package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.change.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.change.TableChangeRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractDocState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableChangeListState extends AbstractDocState {
    private final TableChangeRegistry registry;

    public TableChangeListState(
            UserDocMessageSender sender,
            TableChangeRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage() {
        return "";
    }

    @Override
    protected InputFile getFile() {
        return null;
    }

    @Override
    public UserMenu supportedState() {
        return null;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData userData) {
        return null;
    }
}
