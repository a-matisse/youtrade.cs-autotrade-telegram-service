package cs.youtrade.autotrade.client.util.excel;

import org.apache.poi.xssf.usermodel.XSSFColor;

public class ColorUtils {
    public static XSSFColor getCustomXSSFColor(String hexColor) {
        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }

        byte[] rgb = new byte[3];
        rgb[0] = (byte) Integer.parseInt(hexColor.substring(0, 2), 16);
        rgb[1] = (byte) Integer.parseInt(hexColor.substring(2, 4), 16);
        rgb[2] = (byte) Integer.parseInt(hexColor.substring(4, 6), 16);

        return new XSSFColor(rgb, null);
    }
}
