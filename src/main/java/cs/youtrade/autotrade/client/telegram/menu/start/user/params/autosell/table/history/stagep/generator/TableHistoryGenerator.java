package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.history.stagep.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.ITableGenerator;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetDto;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeOnSellItemMainInfoDto;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeSoldItemMainInfoDto;
import cs.youtrade.autotrade.client.util.excel.XlsxExporter;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.FcdSellHistoryFullDto;
import cs.youtrade.autotrade.client.util.excel.generator.AbstractXlsxGenerator;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Component
public class TableHistoryGenerator
        extends AbstractXlsxGenerator
        implements ITableGenerator<FcdSellHistoryFullDto, File> {
    private static final List<String> utilHeaders = List.of(
            "token-ID", "Steam токен", "Имя токена"
    );
    private static final List<String> mainHeaders = List.of(
            "Дата покупки", "Название"
    );
    private static final List<String> itemHeaders = List.of(
            "Куплено на", "Продано на"
    );
    private static final List<String> sellHeaders = List.of(
            "Куп. $", "Прод. $", "% приб."
    );

    @Override
    public File createFile(FcdSellHistoryFullDto input) throws IOException {
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

    private void fillRow(
            Row row,
            YouTradeSoldItemMainInfoDto item,
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

    private int fillUtil(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getTokenId(),
                item.getSteamToken(),
                item.getGivenName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillDate(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getSoldAt()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillMain(
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

    private int fillItem(
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

    private int fillSell(
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
                profit
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillHeaderRow(
            Sheet sheet,
            int rowNum,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle itemStyle,
            CellStyle sellStyle
    ) {
        Row headerRow = sheet.createRow(rowNum);
        int rOrd = 0;
        rOrd = createHeader(rOrd, headerRow, utilHeaders, utilStyle);
        rOrd = createHeader(rOrd, headerRow, mainHeaders, mainStyle);
        rOrd = createHeader(rOrd, headerRow, itemHeaders, itemStyle);
        return createHeader(rOrd, headerRow, sellHeaders, sellStyle);
    }

    @Override
    public File handleFile(File file) throws IOException {
        return file;
    }
}
