package cs.youtrade.autotrade.client.telegram.prototype.menu.img;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.IMenuEnum;
import cs.youtrade.telegram.buttons.menu.img.AbstractImageMenuState;
import cs.youtrade.telegram.buttons.sender.img.BaseImageMessageSender;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Log4j2
public abstract class YTPImageMenuState<MENU extends IMenuEnum> extends AbstractImageMenuState<UserData, MENU, UserMenu> {
    public YTPImageMenuState(BaseImageMessageSender<UserData> sender) {
        super(sender);
    }

    @Override
    public File getPicture(TelegramClient bot, UserData userData) {
        try(InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(getPicPath(bot, userData))) {
            if (inputStream == null)
                return null;

            // Создаем временный файл
            File tempFile = File.createTempFile(fileName(bot, userData), ".png");
            tempFile.deleteOnExit();
            // Копируем содержимое
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        } catch (IOException e) {
            log.error("Couldn't get image from {}", getClass().getName(), e);
            return null;
        }
    }

    public abstract String getPicPath(TelegramClient bot, UserData userData);

    public String fileName(TelegramClient bot, UserData userData) {
        return "def";
    }
}
