package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.inventory.stage1.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.ITableGenerator;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory.FcdInvV2GetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory.FcdInvV2ItemDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory.FcdInvV2PostDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory.FcdInvV2PostGroupDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.selling.FcdSellingV2PostDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.selling.FcdSellingV2PostGroupDto;
import cs.youtrade.autotrade.client.util.excel.generator.AbstractXlsxGenerator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static cs.youtrade.autotrade.client.util.excel.XlsxParserHelper.getCellString;

@Component
public class TableV2InventoryGenerator
        extends AbstractXlsxGenerator
        implements ITableGenerator<List<FcdInvV2GetDto>, List<FcdInvV2PostGroupDto>> {
    private static final List<String> utilHeaders = List.of(
            "asset-ID"
    );
    private static final List<String> mainHeaders = List.of(
            "Название"
    );
    protected static final List<String> controlHeaders = List.of(
            "Закуп $", "Мин $", "Макс $"
    );
    private static final List<String> flagHeaders = List.of(
            "Запретить"
    );

    @Override
    public File createFile(List<FcdInvV2GetDto> input) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            // Инициализация стилей
            CellStyle utilStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
            CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle controlStyle = createSideStyle(wb, YouTradeColorCodes.CONTROL);
            // Проходимся по каждому аккаунту
            for (var dto : input) {
                var list = dto.getItems();
                list.sort(Comparator.comparing(FcdInvV2ItemDto::getAssetId));
                // Имя таблицы
                Sheet sheet = wb.createSheet(dto.getTokenName());
                // Инициализация заголовков
                int rowIdx = 0;
                int totalColumns = fillHeaderRow(sheet, rowIdx++, utilStyle, mainStyle, controlStyle);
                for (var item : list) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, item, utilStyle, mainStyle, controlStyle);
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
            // Формирование файла для пользователя
            File out = File.createTempFile("inventory", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                wb.write(fos);
                return out;
            }
        }
    }

    private void fillRow(
            Row row,
            FcdInvV2ItemDto item,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle controlStyle
    ) {
        int col = 0;
        col = fillUtil(col, row, item, utilStyle);
        col = fillMain(col, row, item, mainStyle);
        col = fillControl(col, row, item, controlStyle);
        fillFlag(col, row, item, controlStyle);
    }

    private int fillUtil(
            int rOrd,
            Row row,
            FcdInvV2ItemDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getAssetId()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillMain(
            int rOrd,
            Row row,
            FcdInvV2ItemDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getItemName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillControl(
            int rOrd,
            Row row,
            FcdInvV2ItemDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                "",
                "",
                ""
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillFlag(
            int rOrd,
            Row row,
            FcdInvV2ItemDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                Boolean.toString(item.isRestricted()).toUpperCase()
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
        rOrd = createHeader(rOrd, headerRow, controlHeaders, controlStyle);
        return createHeader(rOrd, headerRow, flagHeaders, controlStyle);
    }

    @Override
    public List<FcdInvV2PostGroupDto> handleFile(File file) throws IOException {
        List<FcdInvV2PostGroupDto> toPost = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(file))) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                String tokenName = sheet.getSheetName();
                List<FcdInvV2PostDto> dtos = new ArrayList<>();
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    String idStr = getCellString(row.getCell(0));
                    if (idStr.isEmpty()) continue;

                    String name = getCellString(row.getCell(1));
                    if (name.isEmpty()) continue;

                    String newBaseStr = getCellString(row.getCell(2));
                    if (newBaseStr.isEmpty()) newBaseStr = "0";

                    String newMinStr = getCellString(row.getCell(3));
                    if (newMinStr.isEmpty()) newMinStr = "0";

                    String newMaxStr = getCellString(row.getCell(4));
                    if (newMaxStr.isEmpty()) newMaxStr = "0";

                    if (newMinStr.equals(newMaxStr) && newMinStr.equals(newBaseStr)) continue;

                    String flagStr = Optional.ofNullable(row.getCell(5))
                            .map(Cell::toString)
                            .orElse("FALSE");

                    dtos.add(new FcdInvV2PostDto(
                            idStr,
                            name,
                            newBaseStr,
                            newMinStr,
                            newMaxStr,
                            flagStr
                    ));
                }
                toPost.add(new FcdInvV2PostGroupDto(tokenName, dtos));
            }
            return toPost;
        }
    }
}
