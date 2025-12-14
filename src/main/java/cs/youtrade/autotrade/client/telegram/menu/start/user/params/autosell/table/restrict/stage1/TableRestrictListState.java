package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.restrict.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.AbstractTableState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.restrict.TableRestrictData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.restrict.TableRestrictRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.restrict.stage1.generator.TableRestrictGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict.FcdSellRestrictGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellRestrictEndpoint;
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
public class TableRestrictListState extends AbstractTableState<List<FcdSellRestrictGetDto>> {
    private final TableRestrictRegistry registry;
    private final TableRestrictGenerator generator;
    private final SellRestrictEndpoint endpoint;

    public TableRestrictListState(
            UserDocMessageSender sender,
            TableRestrictRegistry registry,
            TableRestrictGenerator generator,
            SellRestrictEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.generator = generator;
        this.endpoint = endpoint;
    }

    @Override
    public String getHeaderText(UserData user) {
        return """
                📋 Шаблон для управления списком запрещённых к автопродаже предметов
                Заполните новые строки с двумя столбцами: assetId и marketHashName.
                🛑 Внимание: не меняйте название файла и первые ячейки таблицы!
                
                💡 Вы можете использовать онлайн-редактор Excel: https://excel.cloud.microsoft/
                
                Чтобы вернуться назад без изменений, отправьте любое сообщение.
                """;
    }

    @Override
    public List<FcdSellRestrictGetDto> getContent(UserData user) {
        var restAns = endpoint.getRestrictions(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return fcd.getData();
    }

    @Override
    public InputFile getHeaderDoc(UserData user, List<FcdSellRestrictGetDto> content) {
        try {
            return new InputFile(generator.createFile(content), "restricted_all.xlsx");
        } catch (IOException e) {
            log.error("Couldn't create table: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TABLE_RESTRICT_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        Message message = update.getMessage();
        if (!message.hasDocument()) {
            sender.sendTextMes(bot, chatId, "#0: В полученном сообщении не найден документ. Возврат обратно в меню...");
            return UserMenu.TABLE;
        }

        try {
            File tmp = downloadFile(bot, message.getDocument());
            var data = registry.getOrCreate(user, TableRestrictData::new);
            var toPost = generator.handleFile(tmp);
            data.setDtos(toPost);
            return UserMenu.TABLE_RESTRICT_STAGE_P;
        } catch (Exception e) {
            log.error("Ошибка загрузки файла диапазонов", e);
            sender.sendTextMes(bot, chatId, "#1: Не удалось загрузить файл.");
            return UserMenu.TABLE;
        }
    }
}
