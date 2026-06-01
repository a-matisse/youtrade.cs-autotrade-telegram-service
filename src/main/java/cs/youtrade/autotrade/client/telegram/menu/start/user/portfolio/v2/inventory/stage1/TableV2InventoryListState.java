package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.inventory.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.YTPTableState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.inventory.TableV2InventoryData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.inventory.TableV2InventoryRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.inventory.stage1.generator.TableV2InventoryGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory.FcdInvV2GetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.v2.SellV2InventoryEndpoint;
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
public class TableV2InventoryListState extends YTPTableState<List<FcdInvV2GetDto>> {
    private final TableV2InventoryGenerator generator;
    private final SellV2InventoryEndpoint endpoint;
    private final TableV2InventoryRegistry registry;

    public TableV2InventoryListState(
            UserDocMessageSender sender,
            TableV2InventoryGenerator generator,
            SellV2InventoryEndpoint endpoint,
            TableV2InventoryRegistry registry
    ) {
        super(sender);
        this.generator = generator;
        this.endpoint = endpoint;
        this.registry = registry;
    }

    @Override
    public UserMenu executeDocument(TelegramClient bot, Document document, UserData userData) {
        try {
            File tmp = downloadFile(bot, document);
            var data = registry.getOrCreate(userData, TableV2InventoryData::new);
            var toPost = generator.handleFile(tmp);
            data.setDtos(toPost);
            return UserMenu.PORTFOLIO_V2_INVENTORY_STAGE_P;
        } catch (Exception e) {
            log.error("Ошибка загрузки файла диапазонов", e);
            sender.sendTextMes(bot, userData, "#1: Не удалось загрузить файл.");
            return UserMenu.PORTFOLIO;
        }
    }

    @Override
    public List<FcdInvV2GetDto> getContent(UserData userData) {
        var restAns = endpoint.getUploadedItems(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return fcd.getData();
    }

    @Override
    public InputFile getHeaderDoc(UserData userData, List<FcdInvV2GetDto> content) {
        try {
            return new InputFile(generator.createFile(content), "ycs_inventory.xlsx");
        } catch (IOException e) {
            log.error("Couldn't create table: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_V2_INVENTORY_STAGE_1;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Инвентарь пользователя</b>
                        <blockquote><i>Заполните поля, чтобы</i>
                        • <b>Выставить предметы на продажу</b>
                        • <b>Запретить предметы для продажи</b></blockquote>
                        
                        %s <b>Важно</b>
                        <blockquote>• <b>Не меняйте имя файла</b>
                        • <b>Не меняйте первые ячейки таблицы</b>
                        • <b>Не меняйте заполненные ячейки</b></blockquote>
                        """,
                DynamicEmoji.EXCEL.getEmoji(),
                DynamicEmoji.WARNING.getEmoji()
        );
    }
}
