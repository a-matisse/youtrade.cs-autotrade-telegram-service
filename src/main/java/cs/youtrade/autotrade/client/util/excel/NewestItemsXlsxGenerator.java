package cs.youtrade.autotrade.client.util.excel;

import cs.youtrade.autotrade.client.util.autotrade.dto.FcdNewestScoringData;
import cs.youtrade.autotrade.client.util.autotrade.dto.LisItemStatsSummaryDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralNewestDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static cs.youtrade.autotrade.client.util.excel.ColorUtils.getCustomXSSFColor;
import static org.apache.poi.ss.util.WorkbookUtil.createSafeSheetName;

public class NewestItemsXlsxGenerator {
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

    // Цветовая палитра для MEAN (5 цветов для чередования)
    private static final String[] MEAN_BG_COLORS = {
            "#F2F2F2", "#E6F2FF", "#F0E6FF", "#FFF0F0", "#F0FFF0"
    };

    private static final String[] MEAN_TEXT_COLORS = {
            "#666666", "#0D47A1", "#4A148C", "#880E4F", "#1B5E20"
    };

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
            CellStyle mainStyle = createMainStyle(wb);
            CellStyle singleStyle = createSingleStyle(wb);
            CellStyle groupStyle = createGroupStyle(wb);
            List<CellStyle> meanStyles = periods
                    .stream()
                    .map(period -> createMeanStyle(wb, period))
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
            }
            return out;
        }
    }

    private void autoSizeColumns(Sheet sheet, int totalColumns) {
        for (int col = 0; col <= totalColumns; col++)
            sheet.autoSizeColumn(col);
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

    private int setCellValues(
            int rOrd,
            Row row,
            CellStyle style,
            List<Object> objects
    ) {
        for (var value : objects) {
            Cell cell = row.createCell(rOrd++);
            setCellValue(cell, value);
            cell.setCellStyle(style);
        }
        return rOrd;
    }

    private void setCellValue(Cell cell, Object value) {
        switch (value) {
            case String s -> cell.setCellValue(s);
            case Integer i -> cell.setCellValue(i);
            case Double d -> cell.setCellValue(d);
            case Float f -> cell.setCellValue(f.doubleValue());
            case Long l -> cell.setCellValue(l.doubleValue());
            case BigDecimal bd -> cell.setCellValue(bd.doubleValue());
            case LocalDateTime ldt -> {
                Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                cell.setCellValue(date);
            }
            case LocalDate ld -> {
                Date date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
                cell.setCellValue(date);
            }
            case null -> cell.setCellValue("");
            default -> throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        }
    }

    private int fillHeaderRow(
            Sheet sheet, int rowNum,
            CellStyle mainStyle, CellStyle singleStyle, CellStyle groupStyle, List<CellStyle> meanStyles
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

    private int createHeader(int rOrd, Row headerRow, List<String> nms, CellStyle style) {
        for (String name : nms) {
            headerRow.createCell(rOrd).setCellValue(name);
            headerRow.getCell(rOrd).setCellStyle(style);
            rOrd++;
        }
        return rOrd;
    }

    private CellStyle createMainStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();

        // Изменение фона
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Изменение текста
        Font font = wb.createFont();
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    public CellStyle createSingleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();

        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = wb.createFont();
        font.setColor(IndexedColors.DARK_GREEN.getIndex());
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    public CellStyle createGroupStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();

        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = wb.createFont();
        font.setColor(IndexedColors.DARK_YELLOW.getIndex());
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    public static CellStyle createMeanStyle(Workbook wb, int meanNumber) {
        if (meanNumber < 1 || meanNumber > 30)
            throw new IllegalArgumentException("Mean number must be between 1 and 30");

        CellStyle style = wb.createCellStyle();

        // Выбор цвета из палитры (чередование 5 цветов)
        int colorIndex = (meanNumber - 1) % MEAN_BG_COLORS.length;

        XSSFColor bgColor = getCustomXSSFColor(MEAN_BG_COLORS[colorIndex]);
        style.setFillForegroundColor(bgColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = wb.createFont();
        XSSFColor fontColor = getCustomXSSFColor(MEAN_TEXT_COLORS[colorIndex]);
        ((XSSFFont) font).setColor(fontColor);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
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
