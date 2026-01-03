package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.change.stage1.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.ITableGenerator;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.change.FcdSellChangePostDto;
import cs.youtrade.autotrade.client.util.autotrade.util.PriceIntervalUpdateV2Dto;
import cs.youtrade.autotrade.client.util.excel.generator.AbstractXlsxGenerator;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cs.youtrade.autotrade.client.util.excel.XlsxParserHelper.getCellString;

public abstract class AbstractChangeGenerator<I>
        extends AbstractXlsxGenerator
        implements ITableGenerator<I, List<FcdSellChangePostDto>> {
    protected static final List<String> utilHeaders = List.of(
            "youTrade-ID"
    );
    protected static final List<String> mainHeaders = List.of(
            "Название"
    );
    protected static final List<String> sellHeaders = List.of(
            "Закуп $", "Мин $", "Макс $", "Текущ $"
    );
    protected static final List<String> controlHeaders = List.of(
            "Новый закуп $", "Новый мин $", "Новый макс $"
    );

    protected void fillRow(
            Row row,
            PriceIntervalUpdateV2Dto item,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle sellStyle,
            CellStyle flagStyle
    ) {
        int col = 0;
        col = fillUtil(col, row, item, utilStyle);
        col = fillMain(col, row, item, mainStyle);
        col = fillSell(col, row, item, sellStyle);
        fillControl(col, row, flagStyle);
    }

    protected int fillUtil(
            int rOrd,
            Row row,
            PriceIntervalUpdateV2Dto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getYouTradeId()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    protected int fillMain(
            int rOrd,
            Row row,
            PriceIntervalUpdateV2Dto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    protected int fillSell(
            int rOrd,
            Row row,
            PriceIntervalUpdateV2Dto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getMarketPrice(),
                item.getMinPrice(),
                item.getMaxPrice(),
                item.getBasePrice()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    protected int fillControl(
            int rOrd,
            Row row,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                "",
                "",
                ""
        );
        return setCellValues(rOrd, row, style, objects);
    }

    protected int fillHeaderRow(
            Sheet sheet,
            int rowNum,
            CellStyle utilStyle,
            CellStyle mainStyle,
            CellStyle sellStyle,
            CellStyle controlStyle
    ) {
        Row headerRow = sheet.createRow(rowNum);
        int rOrd = 0;
        rOrd = createHeader(rOrd, headerRow, utilHeaders, utilStyle);
        rOrd = createHeader(rOrd, headerRow, mainHeaders, mainStyle);
        rOrd = createHeader(rOrd, headerRow, sellHeaders, sellStyle);
        return createHeader(rOrd, headerRow, controlHeaders, controlStyle);
    }

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

                String newBaseStr = getCellString(row.getCell(6));
                if (newBaseStr.isEmpty()) newBaseStr = "0";

                String newMinStr = getCellString(row.getCell(7));
                if (newMinStr.isEmpty()) newMinStr = "0";

                String newMaxStr = getCellString(row.getCell(8));
                if (newMaxStr.isEmpty()) newMaxStr = "0";

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
