package cs.youtrade.autotrade.client.util.excel.generator;

import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static cs.youtrade.autotrade.client.util.excel.ColorUtils.getCustomXSSFColor;

public abstract class AbstractXlsxGenerator {
    public void autoSizeColumns(Sheet sheet, int totalColumns) {
        for (int col = 0; col <= totalColumns; col++)
            sheet.autoSizeColumn(col);
    }

    public int setCellValues(int rOrd, Row row, CellStyle style, List<Object> objects) {
        for (var value : objects) {
            Cell cell = row.createCell(rOrd++);
            setCellValue(cell, value);
            cell.setCellStyle(style);
        }
        return rOrd;
    }

    public void setCellValue(Cell cell, Object value) {
        switch (value) {
            case String s -> cell.setCellValue(s);
            case Integer i -> cell.setCellValue(i);
            case Double d -> cell.setCellValue(d);
            case Float f -> cell.setCellValue(f.doubleValue());
            case Long l -> cell.setCellValue(l.doubleValue());
            case BigDecimal bd -> cell.setCellValue(bd.doubleValue());
            case LocalDateTime ldt -> cell.setCellValue(ldt);
            case LocalDate ld -> cell.setCellValue(ld);
            case null -> cell.setCellValue("");
            default -> throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        }
    }

    public int createHeader(int rOrd, Row headerRow, List<String> nms, CellStyle style) {
        for (String name : nms) {
            headerRow.createCell(rOrd).setCellValue(name);
            headerRow.getCell(rOrd).setCellStyle(style);
            rOrd++;
        }
        return rOrd;
    }

    public CellStyle createDateStyle(Workbook wb, Supplier<CellStyle> styleFactory) {
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle style = styleFactory.get();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy HH:mm"));
        return style;
    }

    public CellStyle createMainStyle(Workbook wb, YouTradeColorCodes color) {
        return createMainStyle(wb, color, 0);
    }

    public CellStyle createSideStyle(Workbook wb, YouTradeColorCodes color) {
        return createSideStyle(wb, color, 0);
    }

    public CellStyle createMainStyle(Workbook wb, YouTradeColorCodes color, int idx) {
        CellStyle style = wb.createCellStyle();

        // Изменение фона
        XSSFColor bgColor = getCustomXSSFColor(color.getBgColor(idx));
        style.setFillForegroundColor(bgColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Изменение текста
        Font font = wb.createFont();
        XSSFColor fontColor = getCustomXSSFColor(color.getTextColor(idx));
        ((XSSFFont) font).setColor(fontColor);

        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public CellStyle createSideStyle(Workbook wb, YouTradeColorCodes color, int idx) {
        CellStyle style = wb.createCellStyle();

        XSSFColor bgColor = getCustomXSSFColor(color.getBgColor(idx));
        style.setFillForegroundColor(bgColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = wb.createFont();
        XSSFColor fontColor = getCustomXSSFColor(color.getTextColor(idx));
        ((XSSFFont) font).setColor(fontColor);

        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}
