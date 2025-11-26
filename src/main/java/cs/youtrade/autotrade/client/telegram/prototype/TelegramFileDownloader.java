package cs.youtrade.autotrade.client.telegram.prototype;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TelegramFileDownloader {
    public static File downloadFile(TelegramClient bot, Document doc) throws TelegramApiException, IOException {
        GetFile gf = new GetFile(doc.getFileId());
        var tgFile = bot.execute(gf);

        File tmp = File.createTempFile("upload_groups_", ".xlsx");
        try (InputStream is = bot.downloadFileAsStream(tgFile);
             FileOutputStream fos = new FileOutputStream(tmp)) {
            is.transferTo(fos);
        }
        return tmp;
    }
}
