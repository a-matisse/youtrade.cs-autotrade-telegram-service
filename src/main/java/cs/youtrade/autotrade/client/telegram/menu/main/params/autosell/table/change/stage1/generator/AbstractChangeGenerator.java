package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.change.stage1.generator;

import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.ITableGenerator;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change.FcdSellChangePostDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cs.youtrade.autotrade.client.util.XlsxParserHelper.getCellString;

public abstract class AbstractChangeGenerator<I> implements ITableGenerator<I, List<FcdSellChangePostDto>> {
    @Override
    public List<FcdSellChangePostDto> handleFile(File file) throws IOException {
        List<FcdSellChangePostDto> postList = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(file))) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String idStr = getCellString(row.getCell(0));
                if (idStr.isEmpty()) continue;

                String name = getCellString(row.getCell(1));
                if (name.isEmpty()) continue;

                String marketPriceStr = getCellString(row.getCell(2));
                if (marketPriceStr.isEmpty())
                    continue;

                String oldMinStr = getCellString(row.getCell(3));
                if (oldMinStr.isEmpty()) continue;

                String oldMaxStr = getCellString(row.getCell(4));
                if (oldMaxStr.isEmpty()) continue;

                String oldBaseStr = getCellString(row.getCell(5));
                if (oldBaseStr.isEmpty()) continue;

                String newMinStr = getCellString(row.getCell(6));
                if (newMinStr.isEmpty()) newMinStr = "0";

                String newMaxStr = getCellString(row.getCell(7));
                if (newMaxStr.isEmpty()) newMaxStr = "0";

                String newBaseStr = getCellString(row.getCell(8));
                if (newBaseStr.isEmpty()) newBaseStr = "0";

                if (newMinStr.equals(newMaxStr) && newMinStr.equals(newBaseStr)) continue;

                postList.add(
                        new FcdSellChangePostDto(
                                idStr,
                                name,
                                marketPriceStr,
                                oldMinStr,
                                oldMaxStr,
                                oldBaseStr,
                                newMinStr,
                                newMaxStr,
                                newBaseStr
                        )
                );
            }
        }
        return postList;
    }
}
