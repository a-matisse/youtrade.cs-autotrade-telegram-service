package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.upload.stage1.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.ITableGenerator;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict.FcdSellRestrictGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadGroupDto;
import cs.youtrade.autotrade.client.util.autotrade.util.ItemsWithoutPricesWrapped;
import cs.youtrade.autotrade.client.util.excel.generator.AbstractXlsxGenerator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cs.youtrade.autotrade.client.util.excel.XlsxParserHelper.getCellString;

@Component
public class TableUploadGenerator
        extends AbstractXlsxGenerator
        implements ITableGenerator<List<FcdSellUploadGetDto>, List<FcdSellUploadGroupDto>> {
    private static final List<String> utilHeaders = List.of(
            "asset-ID"
    );
    private static final List<String> mainHeaders = List.of(
            "Название"
    );
    protected static final List<String> controlHeaders = List.of(
            "Закуп $", "Мин $", "Макс $"
    );

    @Override
    public File createFile(List<FcdSellUploadGetDto> input) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle utilStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
            CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle controlStyle = createSideStyle(wb, YouTradeColorCodes.CONTROL);

            for (FcdSellUploadGetDto dto : input) {
                // Имя таблицы
                Sheet sheet = wb.createSheet(dto.getTokenName());

                int rowIdx = 0;
                int totalColumns = fillHeaderRow(sheet, rowIdx++, utilStyle, mainStyle, controlStyle);
                for (var item : dto.getInv()) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, item, utilStyle, mainStyle, controlStyle);
                }
                autoSizeColumns(sheet, totalColumns);
            }

            File out = File.createTempFile("inventory_all_", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                wb.write(fos);
                return out;
            }
        }
    }

    private void fillRow(
            Row row,
            ItemsWithoutPricesWrapped.ItemWithoutPrices item,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle controlStyle
    ) {
        int col = 0;
        col = fillUtil(col, row, item, utilStyle);
        col = fillMain(col, row, item, mainStyle);
        fillControl(col, row, item, controlStyle);
    }

    private int fillUtil(
            int rOrd,
            Row row,
            ItemsWithoutPricesWrapped.ItemWithoutPrices item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getId()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillMain(
            int rOrd,
            Row row,
            ItemsWithoutPricesWrapped.ItemWithoutPrices item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getMarketHashName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillControl(
            int rOrd,
            Row row,
            ItemsWithoutPricesWrapped.ItemWithoutPrices item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                "",
                "",
                ""
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillHeaderRow(
            Sheet sheet,
            int rowNum,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle controlStyle
    ) {
        Row headerRow = sheet.createRow(rowNum);
        int rOrd = 0;
        rOrd = createHeader(rOrd, headerRow, utilHeaders, utilStyle);
        rOrd = createHeader(rOrd, headerRow, mainHeaders, mainStyle);
        return createHeader(rOrd, headerRow, controlHeaders, controlStyle);
    }

    @Override
    public List<FcdSellUploadGroupDto> handleFile(File file) throws IOException {
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(file))) {
            List<FcdSellUploadGroupDto> groupsToPlace = new ArrayList<>();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);

                String tokenName = sheet.getSheetName();
                List<FcdSellUploadDto> itemsToPlace = new ArrayList<>();
                for (Row row : sheet) {
                    if (row.getRowNum() == 0)
                        continue;

                    String assetId = getCellString(row.getCell(0));
                    if (assetId.isEmpty())
                        continue;

                    String name = getCellString(row.getCell(1));
                    if (name.isEmpty())
                        continue;

                    String minStr = getCellString(row.getCell(2));
                    String maxStr = getCellString(row.getCell(3));
                    String boughtStr = getCellString(row.getCell(4));

                    itemsToPlace.add(new FcdSellUploadDto(assetId, name, minStr, maxStr, boughtStr));
                }
                if (itemsToPlace.isEmpty())
                    continue;

                groupsToPlace.add(new FcdSellUploadGroupDto(tokenName, itemsToPlace));
            }
            return groupsToPlace;
        }
    }
}
