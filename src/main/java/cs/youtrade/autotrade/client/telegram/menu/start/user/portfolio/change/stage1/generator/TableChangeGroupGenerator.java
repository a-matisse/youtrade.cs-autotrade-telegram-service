package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.change.stage1.generator;

import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change.FcdSellChangeGetDto;
import cs.youtrade.autotrade.client.util.autotrade.util.PriceIntervalUpdateV2Dto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class TableChangeGroupGenerator extends AbstractChangeGenerator<FcdSellChangeGetDto> {
    @Override
    public File createFile(FcdSellChangeGetDto input) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            // Styles creation
            CellStyle utilStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
            CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle sellStyle = createSideStyle(wb, YouTradeColorCodes.GROUP);
            CellStyle controlStyle = createSideStyle(wb, YouTradeColorCodes.CONTROL);

            // Имя таблицы
            Sheet sheet = wb.createSheet(input.getTokenName());

            // Инициализация заголовков
            int rowIdx = 0;
            int totalColumns = fillHeaderRow(sheet, rowIdx++, utilStyle, mainStyle, sellStyle, controlStyle);
            for (PriceIntervalUpdateV2Dto item : input.getChangeList()) {
                Row row = sheet.createRow(rowIdx++);
                fillRow(row, item, utilStyle, mainStyle, sellStyle, controlStyle);
            }
            autoSizeColumns(sheet, totalColumns);

            File tmp = File.createTempFile("price_groups_", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(tmp)) {
                wb.write(fos);
                return tmp;
            }
        }
    }
}
