package cs.youtrade.autotrade.client.telegram.menu.start.user.params.table.upload.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.table.AbstractTableState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.table.upload.TableUploadData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.table.upload.TableUploadRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.table.upload.stage1.generator.TableUploadGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellUploadEndpoint;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Log4j2
public class TableUploadListState extends AbstractTableState<List<FcdSellUploadGetDto>> {
    private final TableUploadRegistry registry;
    private final SellUploadEndpoint endpoint;
    private final TableUploadGenerator generator;

    public TableUploadListState(
            UserDocMessageSender sender,
            TableUploadRegistry registry,
            SellUploadEndpoint endpoint,
            TableUploadGenerator generator
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
        this.generator = generator;
    }

    @Override
    public String getHeaderText(UserData user) {
        return """
                📋 Шаблон для установки ценовых диапазонов
                Пожалуйста, заполните все поля и отправьте этот же файл в ответ на это сообщение.
                🛑 Внимание: не меняйте название файла и первые ячейки таблицы!
                
                💡 Вы можете использовать онлайн-редактор Excel: https://excel.cloud.microsoft/
                
                Чтобы вернуться назад без изменений, отправьте любое сообщение.
                """;
    }

    @Override
    public List<FcdSellUploadGetDto> getContent(UserData user) {
        var restAns = endpoint.getUploadedItems(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return fcd.getData();
    }

    @Override
    public InputFile getHeaderDoc(UserData user, List<FcdSellUploadGetDto> content) {
        try {
            return new InputFile(generator.createFile(content), "sell_listed.xlsx");
        } catch (IOException e) {
            log.error("Couldn't create table: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_UPLOAD_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        Message message = update.getMessage();
        if (!message.hasDocument()) {
            sender.sendTextMes(bot, chatId, "#0: В полученном сообщении не найден документ. Возврат обратно в меню...");
            return UserMenu.PORTFOLIO;
        }

        try {
            File tmp = downloadFile(bot, message.getDocument());
            var data = registry.getOrCreate(user, TableUploadData::new);
            var toPost = generator.handleFile(tmp);
            data.setDtos(toPost);
            return UserMenu.PORTFOLIO_UPLOAD_STAGE_P;
        } catch (Exception e) {
            log.error("Ошибка загрузки файла диапазонов", e);
            sender.sendTextMes(bot, chatId, "#1: Не удалось загрузить файл.");
            return UserMenu.PORTFOLIO;
        }
    }
}
