package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.generator;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.sell.FcdSellHistoryFullDto;
import cs.youtrade.autotrade.client.util.autotrade.util.HistoryDateTimeFormat;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeSoldItemMainInfoDto;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class TableSellHistoryGenerator extends AbstractTableHistoryGenerator<FcdSellHistoryFullDto, YouTradeSoldItemMainInfoDto> {
    @Override
    protected void addSideSummary(Sheet sheet, List<YouTradeSoldItemMainInfoDto> items, int totalColumns) {
        Map<LocalDate, List<YouTradeSoldItemMainInfoDto>> byDate = new TreeMap<>();
        for (var item : items) {
            if (item == null || item.getSoldAt() == null
                    || item.getBuyPrice() == null || item.getCleanSellPrice() == null) continue;
            LocalDate date = HistoryDateTimeFormat.parse(item.getSoldAt()).toLocalDate();
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
        var percentStyle = sheet.getWorkbook().createCellStyle();
        percentStyle.cloneStyleFrom(bodyStyle);
        percentStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("0.00\"%\""));
        var moneyStyle = sheet.getWorkbook().createCellStyle();
        moneyStyle.cloneStyleFrom(bodyStyle);
        moneyStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("$#,##0.00;[Red]-$#,##0.00"));

        Row title = sheet.getRow(3);
        if (title == null) title = sheet.createRow(3);
        title.setHeightInPoints(24);
        for (int i = 0; i < 4; i++) title.createCell(start + i).setCellStyle(titleStyle);
        title.getCell(start).setCellValue("ПРОДАЖИ ПО ДАТАМ");
        sheet.addMergedRegion(new CellRangeAddress(3, 3, start, start + 3));
        Row header = sheet.getRow(4);
        String[] names = {"Дата продажи", "Продаж", "Доход, %", "Прибыль, $"};
        for (int i = 0; i < names.length; i++) {
            var cell = header.createCell(start + i);
            cell.setCellValue(names[i]);
            cell.setCellStyle(headerStyle);
        }
        int rowNumber = 5;
        for (var entry : byDate.entrySet()) {
            Row row = sheet.getRow(rowNumber);
            if (row == null) row = sheet.createRow(rowNumber);
            double buy = entry.getValue().stream().mapToDouble(YouTradeSoldItemMainInfoDto::getBuyPrice).sum();
            double sell = entry.getValue().stream().mapToDouble(YouTradeSoldItemMainInfoDto::getCleanSellPrice).sum();
            double profit = sell - buy;
            var dateCell = row.createCell(start);
            dateCell.setCellValue(entry.getKey());
            dateCell.setCellStyle(dateStyle);
            double[] values = {entry.getValue().size(), buy > 0 ? profit / buy * 100 : 0, profit};
            for (int i = 0; i < values.length; i++) {
                var cell = row.createCell(start + i + 1);
                cell.setCellValue(values[i]);
                cell.setCellStyle(i == 1 ? percentStyle : i == 2 ? moneyStyle : bodyStyle);
            }
            rowNumber++;
        }
        sheet.setColumnWidth(start, 18 * 256);
        sheet.setColumnWidth(start + 1, 16 * 256);
        sheet.setColumnWidth(start + 2, 18 * 256);
        sheet.setColumnWidth(start + 3, 20 * 256);
    }
    @Override
    protected String getReportTitle() {
        return "ИСТОРИЯ ПРОДАЖ";
    }

    @Override
    public int fillUtil(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                idText(item.getTokenId()),
                idText(item.getSteamToken()),
                item.getGivenName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    protected LocalDateTime getHistoryDate(YouTradeSoldItemMainInfoDto item) {
        return HistoryDateTimeFormat.parse(item.getSoldAt());
    }

    @Override
    public int fillDate(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                HistoryDateTimeFormat.parse(item.getBoughtAt()),
                HistoryDateTimeFormat.parse(item.getSoldAt())
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillMain(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getItemName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillItem(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getBoughtOn(),
                item.getSoldOn()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillSell(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        BigDecimal profit = BigDecimal
                .valueOf(item.getCleanSellPercent())
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        List<Object> objects = Arrays.asList(
                item.getBuyPrice(),
                item.getCleanSellPrice(),
                profit,
                BigDecimal.valueOf(item.getCleanSellPrice())
                        .subtract(BigDecimal.valueOf(item.getBuyPrice()))
                        .setScale(2, RoundingMode.HALF_UP)
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public List<String> getUtilHeaders() {
        return List.of("token-ID", "Steam аккаунт", "Имя аккаунта");
    }

    @Override
    public List<String> getMainHeaders() {
        return List.of("Дата покупки", "Дата продажи", "Название");
    }

    @Override
    public List<String> getItemHeaders() {
        return List.of("Куплено на", "Продано на");
    }

    @Override
    public List<String> getSellHeaders() {
        return List.of("Buy, $", "Sell, $", "Profit, %", "Profit, $");
    }
}
