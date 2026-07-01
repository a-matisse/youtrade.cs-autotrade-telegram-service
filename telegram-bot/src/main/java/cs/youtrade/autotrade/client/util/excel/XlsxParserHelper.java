package cs.youtrade.autotrade.client.util.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class XlsxParserHelper {
    public static String getCellString(Cell cell) {
        if (cell == null)
            return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    double num = cell.getNumericCellValue();
                    if (num == Math.floor(num))
                        yield String.valueOf((long) num);
                    else
                        yield String.valueOf(num).replaceAll("\\.0+$", "");
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    public static double parseFlexibleDouble(String input) throws ParseException {
        String s = input.trim();
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            NumberFormat format = NumberFormat.getInstance();
            Number number = format.parse(s);
            return number.doubleValue();
        }
    }

    public static long parseFlexibleLong(String input) throws ParseException {
        String s = input.trim();
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            Number number = format.parse(s);
            return number.longValue();
        }
    }
}
