package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.generator;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.ITableGenerator;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.AbstractPortfolioXlsxGenerator;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetFullCommand;
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
        extends AbstractPortfolioXlsxGenerator
        implements ITableGenerator<T, File> {
    @Override
    public File createFile(T input, LocalizedTexts translations) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            // Styles creation
            CellStyle utilStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
            CellStyle dateStyle = createDateStyle(wb, () -> createSideStyle(wb, YouTradeColorCodes.SINGLE));
            CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle itemStyle = createSideStyle(wb, YouTradeColorCodes.RANDOM);
            CellStyle sellStyle = createSideStyle(wb, YouTradeColorCodes.GROUP);
            CellStyle headerStyle = createHeaderStyle(wb);

            Sheet allHistorySheet = wb.createSheet(translations.get("combined-history"));
            var allHistoryItems = input.getDtos()
                    .stream()
                    .filter(dto -> dto != null && dto.getOnSellList() != null)
                    .flatMap(dto -> dto.getOnSellList().stream())
                    .sorted(Comparator.comparing(
                            this::getHistoryDate,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ))
                    .toList();
            int totalColumns = getUtilHeaders(translations).size() + getMainHeaders(translations).size()
                    + getItemHeaders(translations).size() + getSellHeaders(translations).size();
            int allHistoryRowIdx = createReportHeading(allHistorySheet, totalColumns,
                    getReportTitle(translations), translations.get("all-accounts"), allHistoryItems.size(), translations);
            fillHeaderRow(allHistorySheet, allHistoryRowIdx++,
                    headerStyle, headerStyle, headerStyle, headerStyle, translations);
            for (var item : allHistoryItems) {
                Row row = allHistorySheet.createRow(allHistoryRowIdx++);
                fillRow(row, item, utilStyle, dateStyle, mainStyle, itemStyle, sellStyle);
            }
            finishReportSheet(allHistorySheet, totalColumns);
            addSideSummary(allHistorySheet, allHistoryItems, totalColumns, translations);

            for (var getDto : input.getDtos()) {
                if (getDto == null || getDto.getOnSellList() == null || getDto.getOnSellList().isEmpty()) continue;
                // Sheet creation
                Sheet sheet = wb.createSheet(getDto.getTokenName());

                int rowIdx = createReportHeading(sheet, totalColumns,
                        getReportTitle(translations), getDto.getTokenName(), getDto.getOnSellList().size(), translations);
                fillHeaderRow(sheet, rowIdx++, headerStyle, headerStyle, headerStyle, headerStyle, translations);
                for (var item : getDto.getOnSellList()) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, item, utilStyle, dateStyle, mainStyle, itemStyle, sellStyle);
                }
                finishReportSheet(sheet, totalColumns);
                addSideSummary(sheet, getDto.getOnSellList(), totalColumns, translations);
            }
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
            CellStyle sellStyle,
            LocalizedTexts translations
    ) {
        Row headerRow = sheet.createRow(rowNum);
        int rOrd = 0;
        rOrd = createHeader(rOrd, headerRow, getMainHeaders(translations), mainStyle);
        rOrd = createHeader(rOrd, headerRow, getItemHeaders(translations), itemStyle);
        rOrd = createHeader(rOrd, headerRow, getSellHeaders(translations), sellStyle);
        return createHeader(rOrd, headerRow, getUtilHeaders(translations), utilStyle);
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
    public File handleFile(File file, LocalizedTexts translations) throws IOException {
        return file;
    }

    public abstract int fillUtil(int rOrd, Row row, DTO item, CellStyle style);

    public abstract int fillDate(int rOrd, Row row, DTO item, CellStyle style);

    public abstract int fillMain(int rOrd, Row row, DTO item, CellStyle style);

    public abstract int fillItem(int rOrd, Row row, DTO item, CellStyle style);

    public abstract int fillSell(int rOrd, Row row, DTO item, CellStyle style);

    protected abstract LocalDateTime getHistoryDate(DTO item);

    public abstract List<String> getUtilHeaders(LocalizedTexts translations);

    public abstract List<String> getMainHeaders(LocalizedTexts translations);

    public abstract List<String> getItemHeaders(LocalizedTexts translations);

    public abstract List<String> getSellHeaders(LocalizedTexts translations);

    protected abstract String getReportTitle(LocalizedTexts translations);

    protected void addSideSummary(Sheet sheet, List<DTO> items, int totalColumns, LocalizedTexts translations) {
    }
}
