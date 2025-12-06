package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.upload.stage1.generator;

import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.ITableGenerator;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadGroupDto;
import cs.youtrade.autotrade.client.util.autotrade.util.ItemsWithoutPricesWrapped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cs.youtrade.autotrade.client.util.excel.XlsxParserHelper.getCellString;

@Component
public class TableUploadGenerator implements ITableGenerator<List<FcdSellUploadGetDto>, List<FcdSellUploadGroupDto>> {
    @Override
    public File createFile(List<FcdSellUploadGetDto> input) throws IOException {
        Workbook wb = new XSSFWorkbook();

        for (FcdSellUploadGetDto dto : input) {
            List<ItemsWithoutPricesWrapped.ItemWithoutPrices> inv = dto.getInv();

            // Имя таблицы
            String tableName = dto.getTokenName();
            Sheet sheet = wb.createSheet(tableName);

            // Стили
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle greenStyle = wb.createCellStyle();
            greenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Заголовки
            String[] cols = {"assetId", "name", "min_price", "max_price", "bought_price"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
                if ("min_price".equals(cols[i]) || "max_price".equals(cols[i]) || "bought_price".equals(cols[i]))
                    c.setCellStyle(greenStyle);
            }

            if (inv == null || inv.isEmpty())
                continue;

            // Данные
            for (int r = 0; r < inv.size(); r++) {
                ItemsWithoutPricesWrapped.ItemWithoutPrices it = inv.get(r);
                Row row = sheet.createRow(r + 1);

                row
                        .createCell(0)
                        .setCellValue(it.getId());
                row
                        .createCell(1)
                        .setCellValue(it.getMarketHashName());
                Cell min = row
                        .createCell(2);
                Cell max = row
                        .createCell(3);
                Cell bought = row
                        .createCell(4);

                min.setCellStyle(greenStyle);
                max.setCellStyle(greenStyle);
                bought.setCellStyle(greenStyle);
            }

            for (int i = 0; i < cols.length; i++)
                sheet.autoSizeColumn(i);
        }

        File tmp = new File(System.getProperty("java.io.tmpdir"), "inventory_all.xlsx");
        try (FileOutputStream fos = new FileOutputStream(tmp)) {
            wb.write(fos);
            wb.close();
        }
        return tmp;
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
