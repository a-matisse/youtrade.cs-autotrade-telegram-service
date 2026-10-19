package cs.youtrade.autotrade.client.telegram.localization;

import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.waiting.generator.TableWaitingGenerator;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.stage1.generator.TableV2SellingGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserLanguage;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait.FcdSellWaitDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait.FcdSellWaitFullDto;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeOnSellItemMainInfoDto;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeWaitingItemMainInfoDto;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioLocalizationTest {
    private final BotTexts texts = new BotTexts();

    @Test
    void waitingHeadersAreTranslatedWithoutChangingPercentagesOrAccountNames() throws Exception {
        var item = new YouTradeWaitingItemMainInfoDto();
        item.setTokenId(51L);
        item.setSteamToken("steam-account");
        item.setGivenName("Мой аккаунт");
        item.setAssetId(123L);
        item.setItemName("Test item");
        item.setItemPrice(100d);
        item.setDaysLeft(3);
        item.setCurPrice(105.0123d);
        item.setCurProfit(0.050123d);
        var input = new FcdSellWaitFullDto();
        input.setDtos(List.of(new FcdSellWaitDto(List.of(item), "Мой аккаунт", 51L)));

        for (var language : UserLanguage.values()) {
            var translations = texts.forLanguage(language, "table.TableWaiting");
            var file = new TableWaitingGenerator().createFile(input, translations);
            try (var workbook = WorkbookFactory.create(file)) {
                assertEquals(2, workbook.getNumberOfSheets());
                assertEquals(translations.get("combined-pending-items"), workbook.getSheetName(0));
                assertEquals("Мой аккаунт", workbook.getSheetName(1));
                var sheet = workbook.getSheetAt(0);
                assertEquals(translations.get("name"), sheet.getRow(4).getCell(0).getStringCellValue());
                assertEquals("Мой аккаунт", sheet.getRow(5).getCell(7).getStringCellValue());
                var formatter = new DataFormatter(Locale.US);
                for (int column : new int[]{4, 12}) {
                    var cell = sheet.getRow(5).getCell(column);
                    assertEquals(0.050123d, cell.getNumericCellValue(), 0.0000001d);
                    assertEquals("5.01%", formatter.formatCellValue(cell));
                }
                assertEquals(5.0123d, sheet.getRow(5).getCell(13).getNumericCellValue(), 0.0000001d);
            } finally {
                Files.deleteIfExists(file.toPath());
            }
        }
    }

    @Test
    void editableListingsCanBeUploadedAfterSwitchingLanguage() throws Exception {
        var item = new YouTradeOnSellItemMainInfoDto();
        item.setTokenId(51L);
        item.setSteamToken("steam-account");
        item.setGivenName("Мой аккаунт");
        item.setYouTradeId(123L);
        item.setPurchasedAt(LocalDateTime.of(2026, 10, 19, 12, 30));
        item.setItemName("Test item");
        item.setBuyPrice(10d);
        item.setItemMin(11d);
        item.setItemMax(13d);
        item.setSellPrice(12d);
        item.setSellProfit(new BigDecimal("5.0123"));
        var account = new FcdSellListGetDto();
        account.setTokenName("Мой аккаунт");
        account.setTmTokenId(51L);
        account.setOnSellList(List.of(item));
        var generator = new TableV2SellingGenerator();

        for (var language : UserLanguage.values()) {
            var translations = texts.forLanguage(language, "table.TableV2Selling");
            var file = generator.createFile(List.of(account), translations);
            try {
                try (var input = Files.newInputStream(file.toPath());
                     var workbook = WorkbookFactory.create(input)) {
                    assertTrue(workbook.getSheetAt(0).getProtect());
                    assertEquals(translations.get("combined-listings"), workbook.getSheetName(0));
                    var sheet = workbook.getSheetAt(1);
                    assertEquals(translations.get("new-min"), sheet.getRow(0).getCell(11).getStringCellValue());
                    assertEquals("5.01%", new DataFormatter(Locale.US).formatCellValue(sheet.getRow(1).getCell(9)));
                    sheet.getRow(1).getCell(11).setCellValue(11.5d);
                    try (var output = new FileOutputStream(file)) {
                        workbook.write(output);
                    }
                }
                var otherLanguage = language == UserLanguage.RU ? UserLanguage.EN : UserLanguage.RU;
                var parsed = generator.handleFile(file, translations.inLanguage(otherLanguage));
                assertEquals(1, parsed.size());
                assertEquals("Мой аккаунт", parsed.getFirst().getTokenName());
                assertEquals(1, parsed.getFirst().getDtos().size());
                var changed = parsed.getFirst().getDtos().getFirst();
                assertEquals("123", changed.getIdStr());
                assertEquals("Test item", changed.getName());
                assertEquals(11.5d, Double.parseDouble(changed.getNewMinStr()));
            } finally {
                Files.deleteIfExists(file.toPath());
            }
        }
    }
}
