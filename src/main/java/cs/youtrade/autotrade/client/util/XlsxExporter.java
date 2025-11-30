package cs.youtrade.autotrade.client.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class XlsxExporter {
    public static File exportToXlsxFixed(Map<String, List<Map<String, Object>>> data) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        for (var entry : data.entrySet()) {
            String rawSheetName = entry.getKey() == null ? "Sheet" : entry.getKey();
            String sheetName = org.apache.poi.ss.util.WorkbookUtil.createSafeSheetName(rawSheetName);
            List<Map<String, Object>> items = entry.getValue();
            Sheet sheet = workbook.createSheet(sheetName);

            if (items == null || items.isEmpty()) continue;

            Map<String, Object> firstEntry = items.get(0);
            if (firstEntry == null || firstEntry.isEmpty()) continue;

            // Заголовки (порядок ключей первой записи)
            List<String> headers = new ArrayList<>(firstEntry.keySet());

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }

            // Данные
            CreationHelper creationHelper = workbook.getCreationHelper();
            CellStyle dateStyle = workbook.createCellStyle();
            short df = creationHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss");
            dateStyle.setDataFormat(df);

            for (int r = 0; r < items.size(); r++) {
                Map<String, Object> map = items.get(r);
                Row row = sheet.createRow(r + 1);

                for (int c = 0; c < headers.size(); c++) {
                    String key = headers.get(c);
                    Cell cell = row.createCell(c);

                    Object value = map == null ? null : map.get(key);
                    if (value == null) continue;

                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) value);
                    } else if (value instanceof Date) {
                        cell.setCellStyle(dateStyle);
                        cell.setCellValue((Date) value);
                    } else if (value instanceof Calendar) {
                        cell.setCellStyle(dateStyle);
                        cell.setCellValue((Calendar) value);
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Автоширины
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }
        }

        File file = File.createTempFile("export", ".xlsx");
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        } finally {
            workbook.close();
        }

        return file;
    }

    public static <T> File exportToXlsx(Map<String, List<T>> data) throws IOException {
        // Создаём книгу
        Workbook workbook = new XSSFWorkbook();

        for (var entry : data.entrySet()) {
            String sheetName = entry.getKey();
            List<T> items = entry.getValue();
            Sheet sheet = workbook.createSheet(sheetName);
            if (items == null || items.isEmpty())
                continue;

            // Рефлексия: поля класса
            Class<?> clazz = items.get(0).getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields)
                f.setAccessible(true);

            // Заголовок
            Row headerRow = sheet.createRow(0);
            int toSkipHeader = 0;
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                if (f.isAnnotationPresent(ExcelExclude.class)) {
                    toSkipHeader++;
                    continue;
                }

                Cell cell = headerRow.createCell(i - toSkipHeader);
                String cellValue = f.isAnnotationPresent(ExcelSeparator.class)
                        ? ""
                        : f.getName();
                cell.setCellValue(cellValue);
            }

            // Данные
            for (int r = 0; r < items.size(); r++) {
                T obj = items.get(r);
                Row row = sheet.createRow(r + 1);
                int toSkip = 0;
                for (int c = 0; c < fields.length; c++) {
                    Field f = fields[c];
                    if (f.isAnnotationPresent(ExcelSeparator.class))
                        continue;
                    if (f.isAnnotationPresent(ExcelExclude.class)) {
                        toSkip++;
                        continue;
                    }

                    Cell cell = row.createCell(c - toSkip);
                    Object value;
                    try {
                        value = fields[c].get(obj);
                    } catch (IllegalAccessException e) {
                        value = null;
                    }

                    switch (value) {
                        case null -> {
                        }
                        case Number number -> cell.setCellValue(number.doubleValue());
                        case Boolean b -> cell.setCellValue(b);
                        default -> cell.setCellValue(value.toString());
                    }
                }
            }

            // Авто−ширины столбцов
            for (int i = 0; i < fields.length; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        // Создаём временный файл в системном tmp
        File file = File.createTempFile("export", ".xlsx");
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
        workbook.close();

        return file;
    }

    public static <T> File exportToXlsx(Iterable<T> dataIterable, String sheetName) throws IOException {
        Iterator<T> iterator = dataIterable.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Data iterable is empty.");
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName != null ? sheetName : "Sheet");

        // Определяем класс по первой записи
        T first = iterator.next();
        Class<?> clazz = first.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }

        // Заголовки
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < fields.length; i++) {
            headerRow.createCell(i).setCellValue(toTitleCase(fields[i].getName()));
        }

        // Первая строка
        int rowIdx = 1;
        writeRow(sheet.createRow(rowIdx++), fields, first);

        // Остальные строки
        while (iterator.hasNext()) {
            T item = iterator.next();
            writeRow(sheet.createRow(rowIdx++), fields, item);
        }

        // Автоширина
        for (int i = 0; i < fields.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Сохраняем файл
        File file = File.createTempFile("export", ".xlsx");
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
        workbook.close();

        return file;
    }

    // Вспомогательный метод записи строки
    private static void writeRow(Row row, Field[] fields, Object obj) {
        for (int i = 0; i < fields.length; i++) {
            Object value;
            try {
                value = fields[i].get(obj);
            } catch (IllegalAccessException e) {
                value = "ERR";
            }

            Cell cell = row.createCell(i);
            if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value != null) {
                cell.setCellValue(value.toString());
            }
        }
    }

    private static String toTitleCase(String camelCase) {
        String spaced = camelCase.replaceAll("([a-z])([A-Z]+)", "$1 $2")
                .replaceAll("_", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return spaced.isEmpty()
                ? spaced
                : Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }
}
