package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.change.stage1.generator;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change.FcdSellChangeGetDto;
import cs.youtrade.autotrade.client.util.autotrade.util.PriceIntervalUpdateV2Dto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class TableChangeSingleGenerator extends AbstractChangeGenerator<List<FcdSellChangeGetDto>> {
    @Override
    public File createFile(List<FcdSellChangeGetDto> input) throws IOException {
        Workbook wb = new XSSFWorkbook();
        for (FcdSellChangeGetDto changeGetSingle : input) {
            // Имя таблицы
            String tableName = changeGetSingle.getTokenName();
            Sheet sheet = wb.createSheet(tableName);

            // Styles
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            CellStyle editableStyle = wb.createCellStyle();
            editableStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            editableStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Header
            String[] cols = {"youTradeId", "name", "marketPrice", "min_price", "max_price", "base_price", "new_min_price", "new_max_price", "new_base_price"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
                if (cols[i].startsWith("new_"))
                    c.setCellStyle(editableStyle);
            }

            var intervals = changeGetSingle.getChangeList();
            for (int r = 0; r < intervals.size(); r++) {
                PriceIntervalUpdateV2Dto dto = intervals.get(r);
                Row row = sheet.createRow(r + 1);
                row.createCell(0).setCellValue(dto.getYouTradeId());
                row.createCell(1).setCellValue(dto.getName());
                row.createCell(2).setCellValue(dto.getMarketPrice());
                row.createCell(3).setCellValue(dto.getMinPrice());
                row.createCell(4).setCellValue(dto.getMaxPrice());
                row.createCell(5).setCellValue(dto.getBasePrice());

                Cell cellNewMin = row.createCell(6);
                Cell cellNewMax = row.createCell(7);
                Cell cellNewBase = row.createCell(8);
                cellNewMin.setCellStyle(editableStyle);
                cellNewMax.setCellStyle(editableStyle);
                cellNewBase.setCellStyle(editableStyle);
            }
            for (int i = 0; i < cols.length; i++)
                sheet.autoSizeColumn(i);
        }
        File tmp = new File(System.getProperty("java.io.tmpdir"), "price_intervals.xlsx");
        try (FileOutputStream fos = new FileOutputStream(tmp)) {
            wb.write(fos);
            wb.close();
        }
        return tmp;
    }
}
