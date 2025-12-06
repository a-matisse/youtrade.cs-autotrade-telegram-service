package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.restrict.stage1.generator;

import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.ITableGenerator;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict.FcdSellRestrictGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.restrict.FcdSellRestrictPostDto;
import cs.youtrade.autotrade.client.util.autotrade.util.ItemsWithoutPricesWrapped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static cs.youtrade.autotrade.client.util.excel.XlsxParserHelper.getCellString;

@Component
public class TableRestrictGenerator implements ITableGenerator<
        List<FcdSellRestrictGetDto>,
        List<FcdSellRestrictPostDto>
        > {
    @Override
    public File createFile(List<FcdSellRestrictGetDto> input) throws IOException {
        Workbook wb = new XSSFWorkbook();

        CellStyle redStyle = wb.createCellStyle();
        redStyle.setFillForegroundColor(IndexedColors.RED1.getIndex());
        redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (FcdSellRestrictGetDto restrictDto : input) {
            var items = restrictDto.getItems();
            items.sort(Comparator.comparing(ItemsWithoutPricesWrapped.ItemWithoutPrices::getId));

            // Имя таблицы
            String tableName = restrictDto.getTokenName();
            Sheet sheet = wb.createSheet(String.valueOf(tableName));

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("tokenId");
            header.createCell(1).setCellValue("assetId");
            header.createCell(2).setCellValue("marketHashName");
            header.createCell(3).setCellValue("restricted");
            header.getCell(3).setCellStyle(redStyle);

            int rowIdx = 1;
            for (var item : items) {
                Row row = sheet.createRow(rowIdx++);

                row
                        .createCell(0)
                        .setCellValue(restrictDto.getTmTokenId());

                row
                        .createCell(1)
                        .setCellValue(item.getId());
                row
                        .createCell(2)
                        .setCellValue(item.getMarketHashName());

                Cell restrictedCell = row
                        .createCell(3);

                restrictedCell.setCellValue("FALSE");
                restrictedCell.setCellStyle(redStyle);
            }

            // Data-validation for TRUE/FALSE dropdowns on flags
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            DataValidationConstraint boolConstraint = dvHelper.createExplicitListConstraint(new String[]{"TRUE", "FALSE"});
            CellRangeAddressList addressList = new CellRangeAddressList(
                    1, items.size(), // rows
                    3, 3             // columns for flags
            );
            DataValidation validation = dvHelper.createValidation(boolConstraint, addressList);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);

            // Auto-size columns
            for (int col = 0; col <= 3; col++) sheet.autoSizeColumn(col);
        }

        File out = new File(System.getProperty("java.io.tmpdir"), "restricted_all.xlsx");
        try (FileOutputStream fos = new FileOutputStream(out)) {
            wb.write(fos);
        }
        wb.close();
        return out;
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
