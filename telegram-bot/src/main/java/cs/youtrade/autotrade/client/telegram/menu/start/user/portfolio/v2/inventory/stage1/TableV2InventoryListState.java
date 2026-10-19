package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.inventory.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.YTPTableState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.PortfolioTableRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.inventory.TableV2InventoryData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.inventory.TableV2InventoryRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.inventory.stage1.generator.TableV2InventoryGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory.FcdInvV2GetDto;
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
    private final PortfolioTableRegistry tableRegistry;
    private final TableV2InventoryRegistry registry;

    public TableV2InventoryListState(
            UserDocMessageSender sender,
            TableV2InventoryGenerator generator,
            PortfolioTableRegistry tableRegistry,
            TableV2InventoryRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.generator = generator;
        this.tableRegistry = tableRegistry;
        this.registry = registry;
    }

    @Override
    public UserMenu executeDocument(TelegramClient bot, Document document, UserData userData) {
        var translations = botTexts.forUser(userData, "portfolio.TableV2Inventory");
        try {
            File tmp = downloadFile(bot, document);
            var data = registry.getOrCreate(userData, TableV2InventoryData::new);
            var toPost = generator.handleFile(tmp, botTexts.forUser(userData, "table.TableV2Inventory"));
            data.setDtos(toPost);
            return UserMenu.PORTFOLIO_V2_INVENTORY_STAGE_P;
        } catch (Exception e) {
            log.error("Ошибка загрузки файла диапазонов", e);
            sender.sendTextMes(bot, userData, String.format("#1: %s",
                    translations.get("could-not-download-the-file")));
            return UserMenu.PORTFOLIO;
        }
    }

    @Override
    public List<FcdInvV2GetDto> getContent(UserData userData) {
        var content = tableRegistry
                .getOrCreate(userData, PortfolioTableData::new)
                .getData();
        if (content == null)
            return null;
        return ((List<?>) content)
                .stream()
                .map(FcdInvV2GetDto.class::cast)
                .toList();
    }

    @Override
    public InputFile getHeaderDoc(UserData userData, List<FcdInvV2GetDto> content) {
        try {
            return new InputFile(generator.createFile(content, botTexts.forUser(userData, "table.TableV2Inventory")), "ycs_inventory.xlsx");
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
        var translations = botTexts.forUser(userData, "portfolio.TableV2Inventory");
        return String.format("""
                        %s <b>%s</b>
                        <blockquote><i>%s</i>
                        • <b>%s</b>
                        • <b>%s</b></blockquote>
                        
                        %s <b>%s</b>
                        <blockquote>• <b>%s</b>
                        • <b>%s</b>
                        • <b>%s</b></blockquote>
                        """,
                        DynamicEmoji.EXCEL.getEmoji(),
                        translations.get("your-inventory"),
                        translations.get("fill-in-the-fields-to"),
                        translations.get("list-items-for-sale"),
                        translations.get("block-items-from-sale"),
                        DynamicEmoji.WARNING.getEmoji(),
                        translations.get("important"),
                        translations.get("do-not-rename-the-file"),
                        translations.get("do-not-change-the-first-cells-of"),
                        translations.get("do-not-change-populated-cells"));
    }
}
