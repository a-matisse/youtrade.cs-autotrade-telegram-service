package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.ITableGenerator;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetFullCommand;
import cs.youtrade.autotrade.client.util.excel.generator.AbstractXlsxGenerator;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractTableHistoryGenerator<T extends AbstrFcdSellGetFullCommand<?, DTO>, DTO>
        extends AbstractXlsxGenerator
        implements ITableGenerator<T, File> {
    @Override
    public File createFile(T input) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            // Styles creation
            CellStyle utilStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
            CellStyle dateStyle = createDateStyle(wb, () -> createSideStyle(wb, YouTradeColorCodes.SINGLE));
            CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle itemStyle = createSideStyle(wb, YouTradeColorCodes.RANDOM);
            CellStyle sellStyle = createSideStyle(wb, YouTradeColorCodes.GROUP);
            CellStyle headerStyle = createHeaderStyle(wb);

            Sheet allHistorySheet = wb.createSheet("Общая история");
            var allHistoryItems = input.getDtos()
                    .stream()
                    .filter(dto -> dto != null && dto.getOnSellList() != null)
                    .flatMap(dto -> dto.getOnSellList().stream())
                    .sorted(Comparator.comparing(
                            this::getHistoryDate,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ))
                    .toList();
            int totalColumns = getUtilHeaders().size() + getMainHeaders().size()
                    + getItemHeaders().size() + getSellHeaders().size();
            int allHistoryRowIdx = createReportHeading(allHistorySheet, totalColumns,
                    getReportTitle(), "Все аккаунты", allHistoryItems.size());
            fillHeaderRow(allHistorySheet, allHistoryRowIdx++,
                    headerStyle, headerStyle, headerStyle, headerStyle);
            for (var item : allHistoryItems) {
                Row row = allHistorySheet.createRow(allHistoryRowIdx++);
                fillRow(row, item, utilStyle, dateStyle, mainStyle, itemStyle, sellStyle);
            }

            for (var getDto : input.getDtos()) {
                if (getDto == null || getDto.getOnSellList() == null || getDto.getOnSellList().isEmpty()) continue;
                // Sheet creation
                Sheet sheet = wb.createSheet(getDto.getTokenName());

                int rowIdx = createReportHeading(sheet, totalColumns,
                        getReportTitle(), getDto.getTokenName(), getDto.getOnSellList().size());
                fillHeaderRow(sheet, rowIdx++, headerStyle, headerStyle, headerStyle, headerStyle);
                for (var item : getDto.getOnSellList()) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, item, utilStyle, dateStyle, mainStyle, itemStyle, sellStyle);
                }
                finishReportSheet(sheet, totalColumns);
            }
            finishReportSheet(allHistorySheet, totalColumns);
            File out = File.createTempFile("sell_history_", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                wb.write(fos);
                return out;
            }
        }
    }

    protected int fillHeaderRow(
            Sheet sheet,
            int rowNum,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle itemStyle,
            CellStyle sellStyle
    ) {
        Row headerRow = sheet.createRow(rowNum);
        int rOrd = 0;
        rOrd = createHeader(rOrd, headerRow, getMainHeaders(), mainStyle);
        rOrd = createHeader(rOrd, headerRow, getItemHeaders(), itemStyle);
        rOrd = createHeader(rOrd, headerRow, getSellHeaders(), sellStyle);
        return createHeader(rOrd, headerRow, getUtilHeaders(), utilStyle);
    }

    private void fillRow(
            Row row,
            DTO item,
            CellStyle utilStyle,
            CellStyle dateStyle,
            CellStyle mainStyle,
            CellStyle itemStyle,
            CellStyle sellStyle
    ) {
        int col = 0;
        col = fillDate(col, row, item, dateStyle);
        col = fillMain(col, row, item, mainStyle);
        col = fillItem(col, row, item, itemStyle);
        col = fillSell(col, row, item, sellStyle);
        fillUtil(col, row, item, utilStyle);
    }

    @Override
    public File handleFile(File file) throws IOException {
        return file;
    }

    public abstract int fillUtil(int rOrd, Row row, DTO item, CellStyle style);

    public abstract int fillDate(int rOrd, Row row, DTO item, CellStyle style);

    public abstract int fillMain(int rOrd, Row row, DTO item, CellStyle style);

    public abstract int fillItem(int rOrd, Row row, DTO item, CellStyle style);

    public abstract int fillSell(int rOrd, Row row, DTO item, CellStyle style);

    protected abstract LocalDateTime getHistoryDate(DTO item);

    public abstract List<String> getUtilHeaders();

    public abstract List<String> getMainHeaders();

    public abstract List<String> getItemHeaders();

    public abstract List<String> getSellHeaders();

    protected abstract String getReportTitle();
}
