package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table;

import cs.youtrade.autotrade.client.telegram.prototype.TelegramFileDownloader;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractDocState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;

public abstract class AbstractTableState<C> extends AbstractDocState<C> {
    public AbstractTableState(
            UserDocMessageSender sender
    ) {
        super(sender);
    }

    public File downloadFile(TelegramClient bot, Document doc) throws TelegramApiException, IOException {
        return TelegramFileDownloader.downloadFile(bot, doc);
    }
}
