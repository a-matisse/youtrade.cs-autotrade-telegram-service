package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.waiting.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.ITableGenerator;
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
import java.util.Comparator;
import java.util.List;

@Component
public class TableWaitingGenerator
        extends AbstractXlsxGenerator
        implements ITableGenerator<FcdSellWaitFullDto, File> {
    private static final List<String> utilHeaders = List.of(
            "token-ID", "Steam аккаунт", "Имя аккаунта", "asset-ID"
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
            CellStyle headerStyle = createHeaderStyle(wb);

            Sheet allWaitingSheet = wb.createSheet("Общее ожидание");
            var allWaitingItems = input.getDtos()
                    .stream()
                    .filter(dto -> dto != null && dto.getOnSellList() != null)
                    .flatMap(dto -> dto.getOnSellList().stream())
                    .sorted(Comparator.comparing(
                            YouTradeWaitingItemMainInfoDto::getDaysLeft,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ))
                    .toList();
            int totalColumns = utilHeaders.size() + mainHeaders.size() + sellHeaders.size();
            int allWaitingRowIdx = createReportHeading(allWaitingSheet, totalColumns,
                    "ОЖИДАНИЕ", "Все аккаунты", allWaitingItems.size());
            fillHeaderRow(allWaitingSheet, allWaitingRowIdx++, headerStyle, headerStyle, headerStyle);
            for (var item : allWaitingItems) {
                Row row = allWaitingSheet.createRow(allWaitingRowIdx++);
                fillRow(row, item, utilStyle, mainStyle, sellStyle);
            }
            finishReportSheet(allWaitingSheet, totalColumns);

            for (var dto : input.getDtos()) {
                if (dto == null || dto.getOnSellList() == null || dto.getOnSellList().isEmpty()) continue;
                // Sheet creation
                Sheet sheet = wb.createSheet(dto.getTokenName());

                // Инициализация заголовков
                int rowIdx = createReportHeading(sheet, totalColumns,
                        "ОЖИДАНИЕ", dto.getTokenName(), dto.getOnSellList().size());
                fillHeaderRow(sheet, rowIdx++, headerStyle, headerStyle, headerStyle);
                for (var item : dto.getOnSellList()) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, item, utilStyle, mainStyle, sellStyle);
                }
                finishReportSheet(sheet, totalColumns);
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
        rOrd = createHeader(rOrd, headerRow, mainHeaders, mainStyle);
        rOrd = createHeader(rOrd, headerRow, sellHeaders, sellStyle);
        return createHeader(rOrd, headerRow, utilHeaders, utilStyle);
    }

    @Override
    public File handleFile(File file) throws IOException {
        return file;
    }
}
