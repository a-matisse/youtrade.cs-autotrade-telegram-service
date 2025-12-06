package cs.youtrade.autotrade.client.util.excel;

import org.apache.poi.ss.usermodel.Cell;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class XlsxParserHelper {
    public static String getCellString(Cell cell) {
        return cell == null ? "" : cell.toString().trim();
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
