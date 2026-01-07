package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.restrict.stage1.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.ITableGenerator;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict.FcdSellRestrictGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict.FcdSellRestrictPostDto;
import cs.youtrade.autotrade.client.util.autotrade.util.ItemsWithoutPricesWrapped;
import cs.youtrade.autotrade.client.util.excel.generator.AbstractXlsxGenerator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static cs.youtrade.autotrade.client.util.excel.XlsxParserHelper.getCellString;

@Component
public class TableRestrictGenerator
        extends AbstractXlsxGenerator
        implements ITableGenerator<List<FcdSellRestrictGetDto>, List<FcdSellRestrictPostDto>> {
    private static final List<String> utilHeaders = List.of(
            "token-ID", "asset-ID"
    );
    private static final List<String> mainHeaders = List.of(
            "Название"
    );
    private static final List<String> controlHeaders = List.of(
            "Запретить продавать"
    );

    @Override
    public File createFile(List<FcdSellRestrictGetDto> input) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle utilStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
            CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle controlStyle = createSideStyle(wb, YouTradeColorCodes.CONTROL);

            for (FcdSellRestrictGetDto dto : input) {
                var list = dto.getItems();
                list.sort(Comparator.comparing(ItemsWithoutPricesWrapped.ItemWithoutPrices::getId));

                // Имя таблицы
                Sheet sheet = wb.createSheet(dto.getTokenName());

                // Инициализация заголовков
                int rowIdx = 0;
                int totalColumns = fillHeaderRow(sheet, rowIdx++, utilStyle, mainStyle, controlStyle);
                for (var item : list) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, dto, item, utilStyle, mainStyle, controlStyle);
                }
                autoSizeColumns(sheet, totalColumns);

                // Data-validation for TRUE/FALSE dropdowns on flags
                if (!list.isEmpty()) {
                    int lastColumnIdx = totalColumns - 1;
                    DataValidationHelper dvHelper = sheet.getDataValidationHelper();
                    DataValidationConstraint boolConstraint = dvHelper.createExplicitListConstraint(new String[]{"TRUE", "FALSE"});
                    CellRangeAddressList addressList = new CellRangeAddressList(
                            1, list.size(),
                            lastColumnIdx, lastColumnIdx
                    );
                    DataValidation validation = dvHelper.createValidation(boolConstraint, addressList);
                    validation.setShowErrorBox(true);
                    sheet.addValidationData(validation);
                }
            }

            File out = File.createTempFile("restricted_all_", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                wb.write(fos);
                return out;
            }
        }
    }

    private void fillRow(
            Row row,
            FcdSellRestrictGetDto dto,
            ItemsWithoutPricesWrapped.ItemWithoutPrices item,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle controlStyle
    ) {
        int col = 0;
        col = fillUtil(col, row, dto, item, utilStyle);
        col = fillMain(col, row, item, mainStyle);
        fillControl(col, row, item, controlStyle);
    }

    private int fillUtil(
            int rOrd,
            Row row,
            FcdSellRestrictGetDto dto,
            ItemsWithoutPricesWrapped.ItemWithoutPrices item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                dto.getTmTokenId(),
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
                "FALSE"
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
    public List<FcdSellRestrictPostDto> handleFile(File file) throws IOException {
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(file))) {
            List<FcdSellRestrictPostDto> toSave = new ArrayList<>();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0)
                        continue;

                    String tokenIdStr = getCellString(row.getCell(0));
                    if (tokenIdStr.isEmpty())
                        continue;

                    String assetIdStr = getCellString(row.getCell(1));
                    if (assetIdStr.isEmpty())
                        continue;

                    String name = getCellString(row.getCell(2));
                    if (name.isEmpty())
                        continue;

                    String restrictedStr = getCellString(row.getCell(3));
                    if (restrictedStr.isEmpty())
                        continue;

                    toSave.add(new FcdSellRestrictPostDto(tokenIdStr, assetIdStr, name, restrictedStr));
                }
            }
            return toSave;
        }
    }
}
