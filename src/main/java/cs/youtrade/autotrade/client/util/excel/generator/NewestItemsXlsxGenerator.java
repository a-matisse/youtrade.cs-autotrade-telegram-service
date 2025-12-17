package cs.youtrade.autotrade.client.util.excel.generator;

import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdNewestScoringData;
import cs.youtrade.autotrade.client.util.autotrade.dto.LisItemStatsSummaryDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralNewestDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static org.apache.poi.ss.util.WorkbookUtil.createSafeSheetName;

public class NewestItemsXlsxGenerator extends AbstractXlsxGenerator {
    private static final String SHEET_NAME = "export";

    private static final List<String> mainHdrNms = List.of(
            "Название предмета", "Мин. цена", "Макс. цена", "Наименьшая цена", "Мин. priceFactor", "Макс. priceFactor",
            "Популярность", "Количество обновлений"
    );

    private static final List<String> singleHdrNms = List.of(
            "$ [SINGLE]", "Мин. % [SINGLE]", "Макс. % [SINGLE]"
    );

    private static final List<String> groupHdrNms = List.of(
            "$ [GROUP]", "Мин. % [GROUP]", "Макс. % [GROUP]"
    );

    private final Collection<Integer> periods;
    private final Collection<LisItemStatsSummaryDto> items;

    public NewestItemsXlsxGenerator(
            FcdGeneralNewestDto fcd
    ) {
        this.periods = fcd
                .getPeriods()
                .stream()
                .distinct()
                .toList();
        this.items = fcd.getItems();
    }

    public File generate() throws IOException {
        // Создаем книгу
        try (Workbook wb = new XSSFWorkbook()) {
            // Создаем стили
            CellStyle mainStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
            CellStyle singleStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle groupStyle = createSideStyle(wb, YouTradeColorCodes.GROUP);
            List<CellStyle> meanStyles = periods
                    .stream()
                    .map(period -> createSideStyle(wb, YouTradeColorCodes.RANDOM, period))
                    .toList();

            // Создаем таблицу
            String sheetName = createSafeSheetName(SHEET_NAME);
            Sheet sheet = wb.createSheet(sheetName);

            // Инициализация заголовков
            int rowIdx = 0;
            int totalColumns = fillHeaderRow(sheet, rowIdx++, mainStyle, singleStyle, groupStyle, meanStyles);
            for (LisItemStatsSummaryDto item : items) {
                Row row = sheet.createRow(rowIdx++);
                fillRow(row, item, mainStyle, singleStyle, groupStyle, meanStyles);
            }
            autoSizeColumns(sheet, totalColumns);

            File out = File.createTempFile("sell_listed_", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                wb.write(fos);
                return out;
            }
        }
    }

    private void fillRow(
            Row row,
            LisItemStatsSummaryDto item,
            CellStyle mainStyle,
            CellStyle singleStyle,
            CellStyle groupStyle,
            List<CellStyle> meanStyles
    ) {
        int col = 0;
        col = fillMainRows(col, row, item, mainStyle);
        col = fillSingleRows(col, row, item, singleStyle);
        col = fillGroupRows(col, row, item, groupStyle);
        fillMeanRows(col, row, item, meanStyles);
    }

    private int fillMainRows(
            int rOrd,
            Row row,
            LisItemStatsSummaryDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getItemName(),
                item.getMinPrice(),
                item.getMaxPrice(),
                item.getMinIntermarketPrice(),
                item.getMinPriceFactor(),
                item.getMaxPriceFactor(),
                item.getPopularity(),
                item.getUpdateCount()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillSingleRows(
            int rOrd,
            Row row,
            LisItemStatsSummaryDto item,
            CellStyle style
    ) {
        return fillData(rOrd, row, style, item.getSingleData(), false);
    }

    private int fillGroupRows(
            int rOrd,
            Row row,
            LisItemStatsSummaryDto item,
            CellStyle style
    ) {
        return fillData(rOrd, row, style, item.getGroupData(), false);
    }

    private int fillMeanRows(
            int rOrd,
            Row row,
            LisItemStatsSummaryDto item,
            List<CellStyle> styles
    ) {
        int sOrd = 0;
        for (int period : periods) {
            CellStyle style = styles.get(sOrd++);
            rOrd = fillMeanRow(rOrd, row, item, style, period);
        }
        return rOrd;
    }

    private int fillMeanRow(
            int rOrd,
            Row row,
            LisItemStatsSummaryDto item,
            CellStyle style,
            int period
    ) {
        return fillData(rOrd, row, style, item.getMeanData().get(period), true);
    }

    private int fillData(
            int rOrd,
            Row row,
            CellStyle style,
            FcdNewestScoringData data,
            boolean addTrend
    ) {
        List<Object> objects = new ArrayList<>();
        objects.add(data.getMeanPrice());
        objects.add(data.getMinPercent());
        objects.add(data.getMaxPercent());

        if (addTrend)
            objects.add(data.getTrendScore());

        return setCellValues(rOrd, row, style, objects);
    }

    private int fillHeaderRow(
            Sheet sheet, int rowNum,
            CellStyle mainStyle, CellStyle singleStyle, CellStyle groupStyle,
            List<CellStyle> meanStyles
    ) {
        Row headerRow = sheet.createRow(rowNum);
        int rOrd = 0;
        rOrd = createHeader(rOrd, headerRow, mainHdrNms, mainStyle);
        rOrd = createHeader(rOrd, headerRow, singleHdrNms, singleStyle);
        rOrd = createHeader(rOrd, headerRow, groupHdrNms, groupStyle);
        int styleIdx = 0;
        for (int period : periods) {
            var meanHdrNms = createMeanHeader(period);
            rOrd = createHeader(rOrd, headerRow, meanHdrNms, meanStyles.get(styleIdx++ % meanStyles.size()));
        }
        return rOrd;
    }

    private List<String> createMeanHeader(int period) {
        return List.of(
                "$" + createNamePlate(period),
                "Мин. %" + createNamePlate(period),
                "Макс. %" + createNamePlate(period),
                "Тренд %" + createNamePlate(period)
        );
    }

    private String createNamePlate(int period) {
        return " [MEAN-" + period + "d]";
    }
}
