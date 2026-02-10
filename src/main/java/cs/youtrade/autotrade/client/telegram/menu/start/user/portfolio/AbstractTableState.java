package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.doc.AbstractDocMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;

public abstract class AbstractTableState<C> extends AbstractDocMenuState<C, ClassicTableMenu> {
    public AbstractTableState(
            UserDocMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public ClassicTableMenu getOption(String optionStr) {
        return ClassicTableMenu.valueOf(optionStr);
    }

    @Override
    public ClassicTableMenu[] getOptions() {
        return ClassicTableMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, ClassicTableMenu t) {
        return switch (t) {
            case OPEN_EDITOR, RETURN -> UserMenu.PORTFOLIO;
        };
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData userData) {
        return update.hasCallbackQuery()
                ? super.execute(bot, update, userData)
                : executeDocument(bot, update.getMessage().getDocument(), userData);
    }

    @Override
    public Map<ClassicTableMenu, String> getUrls(UserData user) {
        return Map.of(
                ClassicTableMenu.OPEN_EDITOR, "https://excel.cloud.microsoft/"
        );
    }

    public abstract UserMenu executeDocument(TelegramClient bot, Document document, UserData userData);
}
