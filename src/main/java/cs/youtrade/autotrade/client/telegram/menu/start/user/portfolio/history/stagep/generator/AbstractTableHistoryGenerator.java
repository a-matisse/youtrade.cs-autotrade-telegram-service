package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.history.stagep.generator;

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

            for (var getDto : input.getDtos()) {
                // Sheet creation
                Sheet sheet = wb.createSheet(getDto.getTokenName());

                int rowIdx = 0;
                int totalColumns = fillHeaderRow(sheet, rowIdx++, utilStyle, mainStyle, itemStyle, sellStyle);
                for (var item : getDto.getOnSellList()) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, item, utilStyle, dateStyle, mainStyle, itemStyle, sellStyle);
                }
                autoSizeColumns(sheet, totalColumns);
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
            CellStyle sellStyle
    ) {
        Row headerRow = sheet.createRow(rowNum);
        int rOrd = 0;
        rOrd = createHeader(rOrd, headerRow, getUtilHeaders(), utilStyle);
        rOrd = createHeader(rOrd, headerRow, getMainHeaders(), mainStyle);
        rOrd = createHeader(rOrd, headerRow, getItemHeaders(), itemStyle);
        return createHeader(rOrd, headerRow, getSellHeaders(), sellStyle);
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
        col = fillUtil(col, row, item, utilStyle);
        col = fillDate(col, row, item, dateStyle);
        col = fillMain(col, row, item, mainStyle);
        col = fillItem(col, row, item, itemStyle);
        fillSell(col, row, item, sellStyle);
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

    public abstract List<String> getUtilHeaders();

    public abstract List<String> getMainHeaders();

    public abstract List<String> getItemHeaders();

    public abstract List<String> getSellHeaders();
}
