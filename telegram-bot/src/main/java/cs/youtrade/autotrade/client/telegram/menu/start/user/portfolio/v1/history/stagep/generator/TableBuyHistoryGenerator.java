package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.generator;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.buy.FcdBuyHistoryFullDto;
import cs.youtrade.autotrade.client.util.autotrade.util.HistoryDateTimeFormat;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradePurchasedHistoryDto;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class TableBuyHistoryGenerator extends AbstractTableHistoryGenerator<FcdBuyHistoryFullDto, YouTradePurchasedHistoryDto> {
    @Override
    protected void addSideSummary(Sheet sheet, List<YouTradePurchasedHistoryDto> items, int totalColumns) {
        Map<LocalDate, List<YouTradePurchasedHistoryDto>> byDate = new TreeMap<>();
        for (var item : items) {
            if (item == null || item.getBoughtAt() == null || item.getBuyPrice() == null) continue;
            LocalDate date = HistoryDateTimeFormat.parse(item.getBoughtAt()).toLocalDate();
            byDate.computeIfAbsent(date, ignored -> new java.util.ArrayList<>()).add(item);
        }
        if (byDate.isEmpty()) return;

        int start = totalColumns + 1;
        var titleStyle = createHeaderStyle(sheet.getWorkbook());
        var headerStyle = createHeaderStyle(sheet.getWorkbook());
        var bodyStyle = createSideStyle(sheet.getWorkbook(), cs.youtrade.autotrade.client.util.YouTradeColorCodes.SINGLE);
        var dateStyle = sheet.getWorkbook().createCellStyle();
        dateStyle.cloneStyleFrom(bodyStyle);
        dateStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("dd.mm.yyyy"));
        var moneyStyle = sheet.getWorkbook().createCellStyle();
        moneyStyle.cloneStyleFrom(bodyStyle);
        moneyStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("$#,##0.00;[Red]-$#,##0.00"));

        Row title = sheet.getRow(3);
        if (title == null) title = sheet.createRow(3);
        title.setHeightInPoints(24);
        for (int i = 0; i < 4; i++) title.createCell(start + i).setCellStyle(titleStyle);
        title.getCell(start).setCellValue("ПОКУПКИ ПО ДНЯМ");
        sheet.addMergedRegion(new CellRangeAddress(3, 3, start, start + 3));
        Row header = sheet.getRow(4);
        String[] names = {"Дата покупки", "Предметов", "Сумма, $", "Средняя, $"};
        for (int i = 0; i < names.length; i++) {
            var cell = header.createCell(start + i);
            cell.setCellValue(names[i]);
            cell.setCellStyle(headerStyle);
        }
        int rowNumber = 5;
        for (var entry : byDate.entrySet()) {
            Row row = sheet.getRow(rowNumber);
            if (row == null) row = sheet.createRow(rowNumber);
            double total = entry.getValue().stream().mapToDouble(YouTradePurchasedHistoryDto::getBuyPrice).sum();
            var dateCell = row.createCell(start);
            dateCell.setCellValue(entry.getKey());
            dateCell.setCellStyle(dateStyle);
            var countCell = row.createCell(start + 1);
            countCell.setCellValue(entry.getValue().size());
            countCell.setCellStyle(bodyStyle);
            var totalCell = row.createCell(start + 2);
            totalCell.setCellValue(total);
            totalCell.setCellStyle(moneyStyle);
            var averageCell = row.createCell(start + 3);
            averageCell.setCellValue(total / entry.getValue().size());
            averageCell.setCellStyle(moneyStyle);
            rowNumber++;
        }
        sheet.setColumnWidth(start, 20 * 256);
        sheet.setColumnWidth(start + 1, 16 * 256);
        sheet.setColumnWidth(start + 2, 20 * 256);
        sheet.setColumnWidth(start + 3, 20 * 256);
    }
    @Override
    protected String getReportTitle() {
        return "ИСТОРИЯ ПОКУПОК";
    }

    @Override
    public int fillUtil(int rOrd, Row row, YouTradePurchasedHistoryDto item, CellStyle style) {
        List<Object> objects = Arrays.asList(
                idText(item.getTokenId()),
                idText(item.getSteamToken()),
                item.getGivenName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillDate(int rOrd, Row row, YouTradePurchasedHistoryDto item, CellStyle style) {
        List<Object> objects = Arrays.asList(
                HistoryDateTimeFormat.parse(item.getBoughtAt())
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillMain(int rOrd, Row row, YouTradePurchasedHistoryDto item, CellStyle style) {
        List<Object> objects = Arrays.asList(
                item.getItemName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillItem(int rOrd, Row row, YouTradePurchasedHistoryDto item, CellStyle style) {
        List<Object> objects = Arrays.asList(
                item.getBoughtOn()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillSell(int rOrd, Row row, YouTradePurchasedHistoryDto item, CellStyle style) {
        List<Object> objects = Arrays.asList(
                item.getBuyPrice()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    protected LocalDateTime getHistoryDate(YouTradePurchasedHistoryDto item) {
        return HistoryDateTimeFormat.parse(item.getBoughtAt());
    }

    @Override
    public List<String> getUtilHeaders() {
        return List.of("token-ID", "Steam аккаунт", "Имя аккаунта");
    }

    @Override
    public List<String> getMainHeaders() {
        return List.of("Дата покупки", "Название");
    }

    @Override
    public List<String> getItemHeaders() {
        return List.of("Куплено на");
    }

    @Override
    public List<String> getSellHeaders() {
        return List.of("Куп. $");
    }
}
