package cs.youtrade.autotrade.client.telegram.menu.start.user.params.table.waiting.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.params.table.ITableGenerator;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeWaitingItemMainInfoDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait.FcdSellWaitFullDto;
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
public class TableWaitingGenerator
        extends AbstractXlsxGenerator
        implements ITableGenerator<FcdSellWaitFullDto, File> {
    private static final List<String> utilHeaders = List.of(
            "token-ID", "Steam токен", "Имя токена", "asset-ID"
    );
    protected static final List<String> mainHeaders = List.of(
            "Название"
    );
    protected static final List<String> sellHeaders = List.of(
            "Закуп $", "Разблокировка дн.", "Текущ $", "% приб."
    );

    @Override
    public File createFile(FcdSellWaitFullDto input) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle utilStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
            CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle sellStyle = createSideStyle(wb, YouTradeColorCodes.GROUP);

            for (var dto : input.getDtos()) {
                // Sheet creation
                Sheet sheet = wb.createSheet(dto.getTokenName());

                // Инициализация заголовков
                int rowIdx = 0;
                int totalColumns = fillHeaderRow(sheet, rowIdx++, utilStyle, mainStyle, sellStyle);
                for (var item : dto.getOnSellList()) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, item, utilStyle, mainStyle, sellStyle);
                }
                autoSizeColumns(sheet, totalColumns);
            }

            File out = File.createTempFile("sell_waiting_", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                wb.write(fos);
                return out;
            }
        }
    }

    private void fillRow(
            Row row,
            YouTradeWaitingItemMainInfoDto item,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle sellStyle
    ) {
        int col = 0;
        col = fillUtil(col, row, item, utilStyle);
        col = fillMain(col, row, item, mainStyle);
        fillSell(col, row, item, sellStyle);
    }

    protected int fillUtil(
            int rOrd,
            Row row,
            YouTradeWaitingItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getTokenId(),
                item.getSteamToken(),
                item.getGivenName(),
                item.getAssetId()
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
        BigDecimal profit = BigDecimal
                .valueOf(item.getCurProfit())
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        List<Object> objects = Arrays.asList(
                item.getItemPrice(),
                item.getDaysLeft(),
                item.getCurPrice(),
                profit
        );
        return setCellValues(rOrd, row, style, objects);
    }

    protected int fillHeaderRow(
            Sheet sheet,
            int rowNum,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle sellStyle
    ) {
        Row headerRow = sheet.createRow(rowNum);
        int rOrd = 0;
        rOrd = createHeader(rOrd, headerRow, utilHeaders, utilStyle);
        rOrd = createHeader(rOrd, headerRow, mainHeaders, mainStyle);
        return createHeader(rOrd, headerRow, sellHeaders, sellStyle);
    }

    @Override
    public File handleFile(File file) throws IOException {
        return file;
    }
}
