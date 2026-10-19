package cs.youtrade.autotrade.client.util.excel.generator;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdNewestScoringData;
import cs.youtrade.autotrade.client.util.autotrade.dto.ItemStatsSummaryDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralNewestDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.apache.poi.ss.util.WorkbookUtil.createSafeSheetName;

public class NewestItemsXlsxGenerator extends AbstractXlsxGenerator {


    private static List<String> getMainHdrNms(LocalizedTexts translations) {
        return List.of(
            translations.get("item-name"), translations.get("min-price"), translations.get("max-price"), translations.get("lowest-price"), translations.get("min-pricefactor"), translations.get("max-pricefactor"),
            translations.get("popularity"), translations.get("price-period"), translations.get("price-phase"), translations.get("update-count")
        );
    }

    private static List<String> getSingleHdrNms(LocalizedTexts translations) {
        return List.of(
            "$ [SINGLE]", translations.get("min-single"), translations.get("max-single")
        );
    }

    private static List<String> getGroupHdrNms(LocalizedTexts translations) {
        return List.of(
            "$ [GROUP]", translations.get("min-group"), translations.get("max-group")
        );
    }

    private final Collection<Integer> periods;
    private final Collection<ItemStatsSummaryDto> items;

    public NewestItemsXlsxGenerator(
            FcdGeneralNewestDto fcd
    ) {
        this.periods = Optional.ofNullable(fcd.getPeriods()).orElse(List.of())
                .stream()
                .distinct()
                .toList();
        this.items = Optional.ofNullable(fcd.getItems()).orElse(List.of());
    }

    public File generate(LocalizedTexts translations) throws IOException {
        // Создаем книгу
        try (Workbook wb = new XSSFWorkbook()) {
            // Создаем стили
            CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle singleStyle = createSideStyle(wb, YouTradeColorCodes.RANDOM);
            CellStyle groupStyle = createSideStyle(wb, YouTradeColorCodes.GROUP);
            CellStyle headerStyle = createHeaderStyle(wb);
            List<CellStyle> meanStyles = periods
                    .stream()
                    .map(period -> createSideStyle(wb, YouTradeColorCodes.RANDOM, period))
                    .toList();

            // Создаем таблицу
            String sheetName = createSafeSheetName(translations.get("new-items-2"));
            Sheet sheet = wb.createSheet(sheetName);

            // Инициализация заголовков
            int totalColumns = getMainHdrNms(translations).size() + getSingleHdrNms(translations).size()
                    + getGroupHdrNms(translations).size() + periods.size() * 4;
            int rowIdx = createReportHeading(sheet, totalColumns,
                    translations.get("new-items"), translations.get("selected-period"), items.size(), translations);
            fillHeaderRow(sheet, rowIdx++, headerStyle, translations);
            for (ItemStatsSummaryDto item : items) {
                Row row = sheet.createRow(rowIdx++);
                fillRow(row, item, mainStyle, singleStyle, groupStyle, meanStyles);
            }
            finishReportSheet(sheet, totalColumns);

            File out = File.createTempFile("sell_listed_", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                wb.write(fos);
                return out;
            }
        }
    }

    private void fillRow(
            Row row,
            ItemStatsSummaryDto item,
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
            ItemStatsSummaryDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.itemName(),
                item.minPrice(),
                item.maxPrice(),
                item.minIntermarketPrice(),
                item.minPriceFactor(),
                item.maxPriceFactor(),
                item.popularity(),
                item.pricePeriod(),
                item.pricePhase(),
                item.updateCount()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillSingleRows(
            int rOrd,
            Row row,
            ItemStatsSummaryDto item,
            CellStyle style
    ) {
        return fillData(rOrd, row, style, item.singleData(), false);
    }

    private int fillGroupRows(
            int rOrd,
            Row row,
            ItemStatsSummaryDto item,
            CellStyle style
    ) {
        return fillData(rOrd, row, style, item.groupData(), false);
    }

    private int fillMeanRows(
            int rOrd,
            Row row,
            ItemStatsSummaryDto item,
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
            ItemStatsSummaryDto item,
            CellStyle style,
            int period
    ) {
        return fillData(rOrd, row, style, item.meanData()[period], true);
    }

    private int fillData(
            int rOrd,
            Row row,
            CellStyle style,
            FcdNewestScoringData data,
            boolean addTrend
    ) {
        List<Object> objects = new ArrayList<>();
        BigDecimal meanPrice = BigDecimal
                .valueOf(data.meanPrice())
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal minPercent = BigDecimal
                .valueOf(data.minPercent())
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxPercent = BigDecimal
                .valueOf(data.maxPercent())
                .setScale(2, RoundingMode.HALF_UP);
        objects.add(meanPrice);
        objects.add(minPercent);
        objects.add(maxPercent);

        if (addTrend) {
            BigDecimal trendScore = BigDecimal
                    .valueOf(data.trendScore())
                    .setScale(2, RoundingMode.HALF_UP);
            objects.add(trendScore);
        }

        return setCellValues(rOrd, row, style, objects);
    }

    private int fillHeaderRow(Sheet sheet, int rowNum, CellStyle headerStyle, LocalizedTexts translations) {
        Row headerRow = sheet.createRow(rowNum);
        int rOrd = 0;
        rOrd = createHeader(rOrd, headerRow, getMainHdrNms(translations), headerStyle);
        rOrd = createHeader(rOrd, headerRow, getSingleHdrNms(translations), headerStyle);
        rOrd = createHeader(rOrd, headerRow, getGroupHdrNms(translations), headerStyle);
        for (int period : periods) {
            var meanHdrNms = createMeanHeader(period, translations);
            rOrd = createHeader(rOrd, headerRow, meanHdrNms, headerStyle);
        }
        return rOrd;
    }

    private List<String> createMeanHeader(int period, LocalizedTexts translations) {
        return List.of(
                "$" + createNamePlate(period),
                translations.get("min") + createNamePlate(period),
                translations.get("max") + createNamePlate(period),
                translations.get("trend") + createNamePlate(period)
        );
    }

    private String createNamePlate(int period) {
        return " [MEAN-" + period + "d]";
    }
}
