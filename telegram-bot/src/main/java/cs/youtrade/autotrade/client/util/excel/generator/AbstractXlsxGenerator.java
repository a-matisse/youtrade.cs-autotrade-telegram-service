package cs.youtrade.autotrade.client.util.excel.generator;

import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import static cs.youtrade.autotrade.client.util.excel.ColorUtils.getCustomXSSFColor;

public abstract class AbstractXlsxGenerator {
    private static final String GRAPHITE = "#17191D";
    private static final String BRAND_RED = "#B8323A";
    private static final String INPUT_HEADER = "#7D2E34";
    private static final String RULE = "#E7E4DC";
    private static final String INPUT_RULE = "#DEC4C7";
    private static final String STRIPE = "#FAF8F3";
    private static final String POSITIVE = "#2F7650";
    private static final String MONEY_FORMAT = "$#,##0.00;[Red]-$#,##0.00";
    private static final String PERCENT_FORMAT = "0.00\"%\";[Red]-0.00\"%\"";
    private static final String DATE_FORMAT = "dd.mm.yyyy hh:mm";

    /** Keeps row zero and column positions intact for files the bot accepts back. */
    public void autoSizeColumns(Sheet sheet, int totalColumns) {
        finishSheet(sheet, totalColumns, 0);
    }

    public void finishReportSheet(Sheet sheet, int totalColumns) {
        finishSheet(sheet, totalColumns, 4);
    }

    public int createReportHeading(Sheet sheet, int totalColumns, String title, String account, int count) {
        Workbook wb = sheet.getWorkbook();
        CellStyle titleStyle = createStyle(wb, GRAPHITE, "#FFFFFF", true, 17, GRAPHITE, true);
        CellStyle subtitleStyle = createStyle(wb, GRAPHITE, "#C9C9C6", false, 10, GRAPHITE, true);
        CellStyle accentStyle = createStyle(wb, BRAND_RED, BRAND_RED, false, 8, BRAND_RED, true);

        reportLine(sheet, 0, totalColumns, "Y.CS  /  " + title, titleStyle, 45);
        reportLine(sheet, 1, totalColumns, account + "   •   " + count + " " + itemWord(count), subtitleStyle, 29);
        reportLine(sheet, 2, totalColumns, "", accentStyle, 4);
        sheet.createRow(3).setHeightInPoints(12);
        return 4;
    }

    private void reportLine(Sheet sheet, int rowNumber, int totalColumns,
                            String text, CellStyle style, float height) {
        Row row = sheet.createRow(rowNumber);
        row.setHeightInPoints(height);
        for (int col = 0; col < totalColumns; col++) {
            Cell cell = row.createCell(col);
            cell.setCellStyle(style);
            if (col == 0) cell.setCellValue(text);
        }
        sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, totalColumns - 1));
    }

    private String itemWord(int count) {
        int tens = count % 100;
        if (tens >= 11 && tens <= 14) return "записей";
        return switch (count % 10) {
            case 1 -> "запись";
            case 2, 3, 4 -> "записи";
            default -> "записей";
        };
    }

    private void finishSheet(Sheet sheet, int totalColumns, int headerRowNumber) {
        Row header = sheet.getRow(headerRowNumber);
        if (header == null || totalColumns <= 0) return;

        sheet.setDisplayGridlines(false);
        sheet.setZoom(90);
        sheet.createFreezePane(0, headerRowNumber + 1);
        if (sheet.getLastRowNum() > headerRowNumber)
            sheet.setAutoFilter(new CellRangeAddress(headerRowNumber, sheet.getLastRowNum(), 0, totalColumns - 1));
        if (sheet instanceof XSSFSheet xssfSheet)
            xssfSheet.setTabColor(getCustomXSSFColor(
                    sheet.getWorkbook().getSheetIndex(sheet) == 0 ? BRAND_RED : GRAPHITE));

        Workbook wb = sheet.getWorkbook();
        Map<String, CellStyle> formats = new HashMap<>();
        for (int col = 0; col < totalColumns; col++) {
            Cell headerCell = header.getCell(col);
            String label = headerCell == null ? "" : headerCell.getStringCellValue();
            sheet.setColumnWidth(col, columnWidth(label) * 256);
            String format = valueFormat(label);
            boolean emphasizeResult = label.toLowerCase(Locale.ROOT).contains("приб.")
                    || label.toLowerCase(Locale.ROOT).contains("profit")
                    || label.toLowerCase(Locale.ROOT).contains("тренд");
            for (int rowNum = headerRowNumber + 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                Cell cell = row.getCell(col);
                if (cell == null) continue;
                boolean numeric = cell.getCellType() == CellType.NUMERIC || format != null;
                CellStyle base = cell.getCellStyle();
                boolean stripe = (rowNum - headerRowNumber) % 2 == 0 && isWhiteBody(base);
                int resultSign = emphasizeResult && cell.getCellType() == CellType.NUMERIC
                        ? Double.compare(cell.getNumericCellValue(), 0) : 0;
                String key = base.getIndex() + "/" + format + "/" + numeric + "/" + stripe + "/" + resultSign;
                CellStyle cellStyle = formats.computeIfAbsent(key, ignored -> {
                    CellStyle styled = wb.createCellStyle();
                    styled.cloneStyleFrom(base);
                    styled.setAlignment(numeric ? HorizontalAlignment.RIGHT : HorizontalAlignment.LEFT);
                    if (format != null)
                        styled.setDataFormat(wb.createDataFormat().getFormat(format));
                    if (stripe) {
                        ((XSSFCellStyle) styled).setFillForegroundColor(getCustomXSSFColor(STRIPE));
                        styled.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    }
                    if (resultSign != 0) {
                        XSSFFont font = (XSSFFont) wb.createFont();
                        font.setFontName("Aptos");
                        font.setFontHeightInPoints((short) 11);
                        font.setBold(true);
                        font.setColor(getCustomXSSFColor(resultSign > 0 ? POSITIVE : BRAND_RED));
                        styled.setFont(font);
                    }
                    return styled;
                });
                cell.setCellStyle(cellStyle);
            }
        }

        sheet.setFitToPage(true);
        PrintSetup print = sheet.getPrintSetup();
        print.setLandscape(true);
        print.setFitWidth((short) 1);
        print.setFitHeight((short) 0);
        sheet.getHeader().setLeft("Y.CS  /  " + sheet.getSheetName());
        sheet.getFooter().setRight("&P / &N");
    }

    private boolean isWhiteBody(CellStyle style) {
        if (!(style instanceof XSSFCellStyle xssfStyle)) return false;
        XSSFColor color = xssfStyle.getFillForegroundXSSFColor();
        return color != null && Arrays.equals(color.getRGB(), new byte[]{-1, -1, -1});
    }

    private int columnWidth(String label) {
        String normalized = label.toLowerCase(Locale.ROOT);
        if (normalized.contains("название")) return 50;
        if (normalized.contains("дата")) return 21;
        if (normalized.contains("steam аккаунт") || normalized.contains("имя аккаунта")) return 24;
        if (normalized.contains("pricefactor") || normalized.contains("[mean-")) return 19;
        if (normalized.contains("id")) return 25;
        return Math.max(16, Math.min(25, label.length() + 5));
    }

    private String valueFormat(String label) {
        String normalized = label.toLowerCase(Locale.ROOT);
        if (normalized.contains("дата")) return DATE_FORMAT;
        if (normalized.contains("%")) return PERCENT_FORMAT;
        if (normalized.contains("$") || normalized.contains("цена") && !normalized.contains("period"))
            return MONEY_FORMAT;
        return null;
    }

    public int setCellValues(int rOrd, Row row, CellStyle style, List<Object> objects) {
        row.setHeightInPoints(27);
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
            case MarketType type -> cell.setCellValue(type.getMarketName());
            case null -> cell.setCellValue("");
            default -> throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        }
    }

    /** Use for platform IDs, which must not pass through Excel's 15-digit numeric precision. */
    protected String idText(Object value) {
        return value == null ? null : value.toString();
    }

    public int createHeader(int rOrd, Row headerRow, List<String> names, CellStyle style) {
        headerRow.setHeightInPoints(35);
        for (String name : names) {
            Cell cell = headerRow.createCell(rOrd++);
            cell.setCellValue(name);
            cell.setCellStyle(style);
        }
        return rOrd;
    }

    public CellStyle createHeaderStyle(Workbook wb) {
        return createStyle(wb, GRAPHITE, "#FFFFFF", true, 10, BRAND_RED, true);
    }

    public CellStyle createInputHeaderStyle(Workbook wb) {
        return createStyle(wb, INPUT_HEADER, "#FFFFFF", true, 10, BRAND_RED, true);
    }

    public CellStyle createDateStyle(Workbook wb, Supplier<CellStyle> styleFactory) {
        CellStyle style = styleFactory.get();
        style.setDataFormat(wb.createDataFormat().getFormat(DATE_FORMAT));
        return style;
    }

    public CellStyle createMainStyle(Workbook wb, YouTradeColorCodes color) {
        return createMainStyle(wb, color, 0);
    }

    public CellStyle createSideStyle(Workbook wb, YouTradeColorCodes color) {
        return createSideStyle(wb, color, 0);
    }

    public CellStyle createMainStyle(Workbook wb, YouTradeColorCodes color, int idx) {
        return createStyle(wb, color.getBgColor(idx), color.getTextColor(idx), false, 10, RULE, false);
    }

    public CellStyle createSideStyle(Workbook wb, YouTradeColorCodes color, int idx) {
        String rule = color == YouTradeColorCodes.CONTROL ? INPUT_RULE : RULE;
        return createStyle(wb, color.getBgColor(idx), color.getTextColor(idx), false, 11, rule, false);
    }

    private CellStyle createStyle(Workbook wb, String background, String foreground,
                                  boolean bold, int size, String bottomRule, boolean header) {
        XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
        style.setFillForegroundColor(getCustomXSSFColor(background));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setIndention((short) 1);
        style.setBorderBottom(header ? BorderStyle.MEDIUM : BorderStyle.HAIR);
        style.setBottomBorderColor(getCustomXSSFColor(bottomRule));
        style.setWrapText(header);

        XSSFFont font = (XSSFFont) wb.createFont();
        font.setFontName("Aptos");
        font.setFontHeightInPoints((short) size);
        font.setBold(bold);
        font.setColor(getCustomXSSFColor(foreground));
        style.setFont(font);
        return style;
    }
}
