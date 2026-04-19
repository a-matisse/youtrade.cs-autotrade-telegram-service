package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.upload.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.YTPTableState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.upload.TableUploadData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.upload.TableUploadRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.upload.stage1.generator.TableUploadGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellUploadEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Log4j2
public class TableUploadListState extends YTPTableState<List<FcdSellUploadGetDto>> {
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
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Выставить на продажу</b>
                        └ <i>Заполните желаемые поля в пустых колонках</i>
                        
                        %s <b>Важно</b>
                        <blockquote>• <b>Не меняйте имя файла</b>
                        • <b>Не меняйте первые ячейки таблицы</b>
                        • <b>Не меняйте заполненные ячейки</b></blockquote>
                        """,
                DynamicEmoji.EXCEL.getEmoji(),
                DynamicEmoji.WARNING.getEmoji()
        );
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
    public UserMenu executeDocument(TelegramClient bot, Document document, UserData user) {
        try {
            File tmp = downloadFile(bot, document);
            var data = registry.getOrCreate(user, TableUploadData::new);
            var toPost = generator.handleFile(tmp);
            data.setDtos(toPost);
            return UserMenu.PORTFOLIO_UPLOAD_STAGE_P;
        } catch (Exception e) {
            log.error("Ошибка загрузки файла диапазонов", e);
            sender.sendTextMes(bot, user, "#1: Не удалось загрузить файл.");
            return UserMenu.PORTFOLIO;
        }
    }
}
