package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.waiting.generator;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.ITableGenerator;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.AbstractPortfolioXlsxGenerator;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeWaitingItemMainInfoDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait.FcdSellWaitFullDto;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class TableWaitingGenerator
        extends AbstractPortfolioXlsxGenerator
        implements ITableGenerator<FcdSellWaitFullDto, File> {
    private static List<String> getUtilHeaders(LocalizedTexts translations) {
        return List.of(
            "token-ID", translations.get("steam-account"), translations.get("account-name"), "asset-ID"
        );
    }
    protected static List<String> getMainHeaders(LocalizedTexts translations) {
        return List.of(
            translations.get("name")
        );
    }
    protected static List<String> getSellHeaders(LocalizedTexts translations) {
        return List.of(
            translations.get("buy"), translations.get("unlock-in-days"), translations.get("current"), String.format("%% %s",
                    translations.get("return-2"))
        );
    }

    @Override
    public File createFile(FcdSellWaitFullDto input, LocalizedTexts translations) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle utilStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
            CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle sellStyle = createSideStyle(wb, YouTradeColorCodes.GROUP);
            CellStyle headerStyle = createHeaderStyle(wb);

            Sheet allWaitingSheet = wb.createSheet(translations.get("combined-pending-items"));
            var allWaitingItems = input.getDtos()
                    .stream()
                    .filter(dto -> dto != null && dto.getOnSellList() != null)
                    .flatMap(dto -> dto.getOnSellList().stream())
                    .sorted(Comparator.comparing(
                            YouTradeWaitingItemMainInfoDto::getDaysLeft,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ))
                    .toList();
            int totalColumns = getUtilHeaders(translations).size() + getMainHeaders(translations).size() + getSellHeaders(translations).size();
            int allWaitingRowIdx = createReportHeading(allWaitingSheet, totalColumns,
                    translations.get("pending"), translations.get("all-accounts"), allWaitingItems.size(), translations);
            fillHeaderRow(allWaitingSheet, allWaitingRowIdx++, headerStyle, headerStyle, headerStyle, translations);
            for (var item : allWaitingItems) {
                Row row = allWaitingSheet.createRow(allWaitingRowIdx++);
                fillRow(row, item, utilStyle, mainStyle, sellStyle);
            }
            finishReportSheet(allWaitingSheet, totalColumns);
            addSideSummary(allWaitingSheet, allWaitingItems, totalColumns, translations);

            for (var dto : input.getDtos()) {
                if (dto == null || dto.getOnSellList() == null || dto.getOnSellList().isEmpty()) continue;
                // Sheet creation
                Sheet sheet = wb.createSheet(dto.getTokenName());

                // Инициализация заголовков
                int rowIdx = createReportHeading(sheet, totalColumns,
                        translations.get("pending"), dto.getTokenName(), dto.getOnSellList().size(), translations);
                fillHeaderRow(sheet, rowIdx++, headerStyle, headerStyle, headerStyle, translations);
                for (var item : dto.getOnSellList()) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, item, utilStyle, mainStyle, sellStyle);
                }
                finishReportSheet(sheet, totalColumns);
                addSideSummary(sheet, dto.getOnSellList(), totalColumns, translations);
            }

            File out = File.createTempFile("sell_waiting_", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                wb.write(fos);
                return out;
            }
        }
    }

    private void addSideSummary(Sheet sheet, List<YouTradeWaitingItemMainInfoDto> items, int totalColumns, LocalizedTexts translations) {
        Map<Integer, List<YouTradeWaitingItemMainInfoDto>> byDays = new TreeMap<>();
        for (var item : items) {
            if (item == null || item.getDaysLeft() == null || item.getItemPrice() == null
                    || item.getCurProfit() == null) continue;
            byDays.computeIfAbsent(item.getDaysLeft(), ignored -> new java.util.ArrayList<>()).add(item);
        }
        if (byDays.isEmpty()) return;

        int start = totalColumns + 1;
        var titleStyle = createHeaderStyle(sheet.getWorkbook());
        var headerStyle = createHeaderStyle(sheet.getWorkbook());
        var bodyStyle = createSideStyle(sheet.getWorkbook(), YouTradeColorCodes.SINGLE);
        var percentStyle = sheet.getWorkbook().createCellStyle();
        percentStyle.cloneStyleFrom(bodyStyle);
        percentStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("0.00%;[Red]-0.00%"));
        var moneyStyle = sheet.getWorkbook().createCellStyle();
        moneyStyle.cloneStyleFrom(bodyStyle);
        moneyStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("$#,##0.00;[Red]-$#,##0.00"));

        Row title = sheet.getRow(3);
        if (title == null) title = sheet.createRow(3);
        title.setHeightInPoints(24);
        for (int i = 0; i < 4; i++) title.createCell(start + i).setCellStyle(titleStyle);
        title.getCell(start).setCellValue(translations.get("return-by-time-to-unlock"));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, start, start + 3));
        Row header = sheet.getRow(4);
        String[] names = {translations.get("days-left"), translations.get("items"), translations.get("return"), translations.get("profit")};
        for (int i = 0; i < names.length; i++) {
            var cell = header.createCell(start + i);
            cell.setCellValue(names[i]);
            cell.setCellStyle(headerStyle);
        }
        int rowNumber = 5;
        for (var entry : byDays.entrySet()) {
            Row row = sheet.getRow(rowNumber);
            if (row == null) row = sheet.createRow(rowNumber);
            BigDecimal invested = BigDecimal.ZERO;
            BigDecimal netProfit = BigDecimal.ZERO;
            for (var item : entry.getValue()) {
                BigDecimal buyPrice = BigDecimal.valueOf(item.getItemPrice());
                invested = invested.add(buyPrice);
                netProfit = netProfit.add(buyPrice.multiply(BigDecimal.valueOf(item.getCurProfit())));
            }
            double[] values = {entry.getKey(), entry.getValue().size(), invested.signum() > 0
                    ? netProfit.divide(invested, 6, RoundingMode.HALF_UP).doubleValue() : 0,
                    netProfit.doubleValue()};
            for (int i = 0; i < values.length; i++) {
                var cell = row.createCell(start + i);
                cell.setCellValue(values[i]);
                cell.setCellStyle(i == 2 ? percentStyle : i == 3 ? moneyStyle : bodyStyle);
            }
            rowNumber++;
        }
        sheet.setColumnWidth(start, 18 * 256);
        sheet.setColumnWidth(start + 1, 16 * 256);
        sheet.setColumnWidth(start + 2, 18 * 256);
        sheet.setColumnWidth(start + 3, 20 * 256);
    }

    private void fillRow(
            Row row,
            YouTradeWaitingItemMainInfoDto item,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle sellStyle
    ) {
        int col = 0;
        col = fillMain(col, row, item, mainStyle);
        col = fillSell(col, row, item, sellStyle);
        fillUtil(col, row, item, utilStyle);
    }

    protected int fillUtil(
            int rOrd,
            Row row,
            YouTradeWaitingItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                idText(item.getTokenId()),
                idText(item.getSteamToken()),
                item.getGivenName(),
                idText(item.getAssetId())
        );
        return setCellValues(rOrd, row, style, objects);
    }

    protected int fillMain(
            int rOrd,
            Row row,
            YouTradeWaitingItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getItemName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    protected int fillSell(
            int rOrd,
            Row row,
            YouTradeWaitingItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getItemPrice(),
                item.getDaysLeft(),
                item.getCurPrice(),
                item.getCurProfit()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    protected int fillHeaderRow(
            Sheet sheet,
            int rowNum,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle sellStyle,
            LocalizedTexts translations
    ) {
        Row headerRow = sheet.createRow(rowNum);
        int rOrd = 0;
        rOrd = createHeader(rOrd, headerRow, getMainHeaders(translations), mainStyle);
        rOrd = createHeader(rOrd, headerRow, getSellHeaders(translations), sellStyle);
        return createHeader(rOrd, headerRow, getUtilHeaders(translations), utilStyle);
    }

    @Override
    public File handleFile(File file, LocalizedTexts translations) throws IOException {
        return file;
    }
}
