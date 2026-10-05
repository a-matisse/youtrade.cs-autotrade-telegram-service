package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.stage1.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.ITableGenerator;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.AbstractPortfolioXlsxGenerator;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.selling.FcdSellingV2PostDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.selling.FcdSellingV2PostGroupDto;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeOnSellItemMainInfoDto;
import cs.youtrade.autotrade.client.util.excel.XlsxParserHelper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static cs.youtrade.autotrade.client.util.excel.XlsxParserHelper.getCellString;

@Service
public class TableV2SellingGenerator
        extends AbstractPortfolioXlsxGenerator
        implements ITableGenerator<List<FcdSellListGetDto>, List<FcdSellingV2PostGroupDto>> {
    private static final String ALL_SHEET = "Общая витрина";
    private static final List<String> utilHeaders = List.of(
            "ID аккаунта", "Имя аккаунта", "youTrade-ID"
    );
    private static final List<String> mainHeaders = List.of(
            "Дата покупки", "Название"
    );
    private static final List<String> sellHeaders = List.of(
            "Закуп $", "Мин $", "Макс $", "Текущ $", "% приб."
    );
    protected static final List<String> controlHeaders = List.of(
            "Новый закуп $", "Новый мин $", "Новый макс $"
    );
    private static final List<String> flagHeaders = List.of(
            "Снять с продажи"
    );

    @Override
    public File createFile(List<FcdSellListGetDto> input) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            // Styles creation
            CellStyle utilStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
            CellStyle dateStyle = createDateStyle(wb, () -> createSideStyle(wb, YouTradeColorCodes.SINGLE));
            CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle sellStyle = createSideStyle(wb, YouTradeColorCodes.GROUP);
            CellStyle controlStyle = createSideStyle(wb, YouTradeColorCodes.CONTROL);
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle inputHeaderStyle = createInputHeaderStyle(wb);

            var allItems = input.stream()
                    .filter(dto -> dto != null && dto.getOnSellList() != null)
                    .flatMap(dto -> dto.getOnSellList().stream()
                            .filter(item -> item != null)
                            .map(item -> new SellRow(dto, item)))
                    .sorted(Comparator.comparing(row -> row.item().getPurchasedAt(),
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
            int reportColumns = utilHeaders.size() + mainHeaders.size() + sellHeaders.size();
            Sheet allSheet = wb.createSheet(ALL_SHEET);
            int allRowIdx = createReportHeading(allSheet, reportColumns,
                    "ВИТРИНА", "Все аккаунты", allItems.size());
            fillReportHeader(allSheet, allRowIdx++, headerStyle);
            for (var item : allItems) {
                Row row = allSheet.createRow(allRowIdx++);
                fillReportRow(row, item, utilStyle, dateStyle, mainStyle, sellStyle);
            }
            finishReportSheet(allSheet, reportColumns);
            addSideSummary(allSheet, allItems, reportColumns);
            allSheet.protectSheet("Y.CS");

            for (var dto : input) {
                if (dto == null || dto.getOnSellList() == null || dto.getOnSellList().isEmpty()) continue;
                // Sheet creation
                var list = dto.getOnSellList();
                Sheet sheet = wb.createSheet(dto.getTokenName());

                // Инициализация заголовков
                int rowIdx = 0;
                int totalColumns = fillHeaderRow(sheet, rowIdx++, headerStyle, headerStyle,
                        headerStyle, inputHeaderStyle);
                for (var item : list) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, dto, item, utilStyle, dateStyle, mainStyle, sellStyle, controlStyle);
                }
                autoSizeColumns(sheet, totalColumns);

                // Validation for TRUE/FALSE
                if (!list.isEmpty()) {
                    int lastColumnIdx = totalColumns - 1;
                    DataValidationHelper dvHelper = sheet.getDataValidationHelper();
                    DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(new String[]{"TRUE", "FALSE"});
                    CellRangeAddressList addressList = new CellRangeAddressList(
                            1, list.size(),
                            lastColumnIdx, lastColumnIdx
                    );
                    DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
                    validation.setShowErrorBox(true);
                    sheet.addValidationData(validation);
                }
            }

            File out = File.createTempFile("sell_listed_", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                wb.write(fos);
                return out;
            }
        }
    }

    private void fillReportHeader(Sheet sheet, int rowNumber, CellStyle style) {
        Row row = sheet.createRow(rowNumber);
        int col = createHeader(0, row, utilHeaders, style);
        col = createHeader(col, row, mainHeaders, style);
        createHeader(col, row, sellHeaders, style);
    }

    private void fillReportRow(Row row, SellRow entry,
                               CellStyle utilStyle, CellStyle dateStyle,
                               CellStyle mainStyle, CellStyle sellStyle) {
        int col = fillUtil(0, row, entry.account(), entry.item(), utilStyle);
        col = fillDate(col, row, entry.item(), dateStyle);
        col = fillMain(col, row, entry.item(), mainStyle);
        fillSell(col, row, entry.item(), sellStyle);
    }

    private void addSideSummary(Sheet sheet, List<SellRow> items, int totalColumns) {
        Map<LocalDate, List<YouTradeOnSellItemMainInfoDto>> byDate = new TreeMap<>();
        for (var entry : items) {
            var item = entry.item();
            if (item.getPurchasedAt() == null) continue;
            byDate.computeIfAbsent(item.getPurchasedAt().toLocalDate(), ignored -> new ArrayList<>()).add(item);
        }
        if (byDate.isEmpty()) return;

        int start = totalColumns + 1;
        var titleStyle = createHeaderStyle(sheet.getWorkbook());
        var headerStyle = createHeaderStyle(sheet.getWorkbook());
        var bodyStyle = createSideStyle(sheet.getWorkbook(), YouTradeColorCodes.SINGLE);
        var dateStyle = sheet.getWorkbook().createCellStyle();
        dateStyle.cloneStyleFrom(bodyStyle);
        dateStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("dd.mm.yyyy"));
        var percentStyle = sheet.getWorkbook().createCellStyle();
        percentStyle.cloneStyleFrom(bodyStyle);
        percentStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("0.00%;[Red]-0.00%"));
        var moneyStyle = sheet.getWorkbook().createCellStyle();
        moneyStyle.cloneStyleFrom(bodyStyle);
        moneyStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("$#,##0.00;[Red]-$#,##0.00"));

        Row title = sheet.getRow(3);
        title.setHeightInPoints(24);
        for (int i = 0; i < 4; i++) title.createCell(start + i).setCellStyle(titleStyle);
        title.getCell(start).setCellValue("ВИТРИНА ПО ДНЯМ ПОКУПКИ");
        sheet.addMergedRegion(new CellRangeAddress(3, 3, start, start + 3));
        Row header = sheet.getRow(4);
        String[] names = {"Дата покупки", "Предметов", "Доход, %", "Прибыль, $"};
        for (int i = 0; i < names.length; i++) {
            var cell = header.createCell(start + i);
            cell.setCellValue(names[i]);
            cell.setCellStyle(headerStyle);
        }
        int rowNumber = 5;
        for (var entry : byDate.entrySet()) {
            Row row = sheet.getRow(rowNumber);
            if (row == null) row = sheet.createRow(rowNumber);
            BigDecimal invested = BigDecimal.ZERO;
            BigDecimal profit = BigDecimal.ZERO;
            for (var item : entry.getValue()) {
                if (item.getBuyPrice() == null || item.getSellProfit() == null) continue;
                BigDecimal buyPrice = BigDecimal.valueOf(item.getBuyPrice());
                invested = invested.add(buyPrice);
                profit = profit.add(buyPrice.multiply(percentPoints(item.getSellProfit())));
            }
            var dateCell = row.createCell(start);
            dateCell.setCellValue(entry.getKey());
            dateCell.setCellStyle(dateStyle);
            var countCell = row.createCell(start + 1);
            countCell.setCellValue(entry.getValue().size());
            countCell.setCellStyle(bodyStyle);
            var percentCell = row.createCell(start + 2);
            percentCell.setCellValue(invested.signum() > 0
                    ? profit.divide(invested, 6, RoundingMode.HALF_UP).doubleValue() : 0);
            percentCell.setCellStyle(percentStyle);
            var profitCell = row.createCell(start + 3);
            profitCell.setCellValue(profit.doubleValue());
            profitCell.setCellStyle(moneyStyle);
            rowNumber++;
        }
        sheet.setColumnWidth(start, 20 * 256);
        sheet.setColumnWidth(start + 1, 16 * 256);
        sheet.setColumnWidth(start + 2, 18 * 256);
        sheet.setColumnWidth(start + 3, 20 * 256);
    }

    private void fillRow(
            Row row,
            FcdSellListGetDto getDto,
            YouTradeOnSellItemMainInfoDto item,
            CellStyle utilStyle,
            CellStyle dateStyle,
            CellStyle mainStyle,
            CellStyle sellStyle,
            CellStyle flagStyle
    ) {
        int col = 0;
        col = fillUtil(col, row, getDto, item, utilStyle);
        col = fillDate(col, row, item, dateStyle);
        col = fillMain(col, row, item, mainStyle);
        col = fillSell(col, row, item, sellStyle);
        col = fillControl(col, row, flagStyle);
        fillFlag(col, row, flagStyle);
    }

    private int fillUtil(
            int rOrd,
            Row row,
            FcdSellListGetDto getDto,
            YouTradeOnSellItemMainInfoDto item,
            CellStyle style
    ) {
        return setCellValues(rOrd, row, style, Arrays.asList(
                idText(getDto.getTmTokenId()), item.getGivenName(), idText(item.getYouTradeId())));
    }

    private int fillDate(
            int rOrd,
            Row row,
            YouTradeOnSellItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getPurchasedAt()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillMain(
            int rOrd,
            Row row,
            YouTradeOnSellItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getItemName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillSell(
            int rOrd,
            Row row,
            YouTradeOnSellItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getBuyPrice(),
                item.getItemMin(),
                item.getItemMax(),
                item.getSellPrice(),
                percentPoints(item.getSellProfit())
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillControl(
            int rOrd,
            Row row,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                "",
                "",
                ""
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillFlag(
            int rOrd,
            Row row,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                "FALSE"
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillHeaderRow(
            Sheet sheet,
            int rowNum,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle sellStyle,
            CellStyle controlStyle
    ) {
        Row headerRow = sheet.createRow(rowNum);
        int rOrd = 0;
        rOrd = createHeader(rOrd, headerRow, utilHeaders, utilStyle);
        rOrd = createHeader(rOrd, headerRow, mainHeaders, mainStyle);
        rOrd = createHeader(rOrd, headerRow, sellHeaders, sellStyle);
        rOrd = createHeader(rOrd, headerRow, controlHeaders, controlStyle);
        return createHeader(rOrd, headerRow, flagHeaders, controlStyle);
    }

    @Override
    public List<FcdSellingV2PostGroupDto> handleFile(File file) throws IOException {
        List<FcdSellingV2PostGroupDto> toPost = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(file))) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                if (ALL_SHEET.equals(sheet.getSheetName())) continue;
                String tokenName = sheet.getSheetName();
                List<FcdSellingV2PostDto> dtos = new ArrayList<>();
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    String idStr = getCellString(row.getCell(2));
                    if (idStr.isEmpty()) continue;

                    String name = getCellString(row.getCell(4));
                    if (name.isEmpty()) continue;

                    String oldBaseStr = getCellString(row.getCell(5));
                    if (oldBaseStr.isEmpty()) continue;

                    String oldMinStr = getCellString(row.getCell(6));
                    if (oldMinStr.isEmpty()) continue;

                    String oldMaxStr = getCellString(row.getCell(7));
                    if (oldMaxStr.isEmpty()) continue;

                    String marketPriceStr = getCellString(row.getCell(8));
                    if (marketPriceStr.isEmpty()) continue;

                    String newBaseStr = getCellString(row.getCell(10));
                    if (newBaseStr.isEmpty()) newBaseStr = "0";

                    String newMinStr = getCellString(row.getCell(11));
                    if (newMinStr.isEmpty()) newMinStr = "0";

                    String newMaxStr = getCellString(row.getCell(12));
                    if (newMaxStr.isEmpty()) newMaxStr = "0";

                    String flagStr = Optional.ofNullable(row.getCell(13))
                            .map(XlsxParserHelper::getCellString)
                            .orElse("FALSE");

                    if (newMinStr.equals(newMaxStr)
                        && newMinStr.equals(newBaseStr)
                        && flagStr.equals("FALSE"))
                        continue;

                    dtos.add(new FcdSellingV2PostDto(
                            idStr,
                            name,
                            marketPriceStr,
                            oldMinStr,
                            oldMaxStr,
                            oldBaseStr,
                            newMinStr,
                            newMaxStr,
                            newBaseStr,
                            flagStr
                    ));
                }
                toPost.add(new FcdSellingV2PostGroupDto(tokenName, dtos));
            }
            return toPost;
        }
    }

    private record SellRow(FcdSellListGetDto account, YouTradeOnSellItemMainInfoDto item) {
    }
}
