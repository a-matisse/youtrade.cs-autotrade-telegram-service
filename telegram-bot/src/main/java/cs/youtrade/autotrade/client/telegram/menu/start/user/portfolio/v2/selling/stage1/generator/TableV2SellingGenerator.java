package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.selling.stage1.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.ITableGenerator;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.selling.FcdSellingV2PostDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.selling.FcdSellingV2PostGroupDto;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeOnSellItemMainInfoDto;
import cs.youtrade.autotrade.client.util.excel.XlsxParserHelper;
import cs.youtrade.autotrade.client.util.excel.generator.AbstractXlsxGenerator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static cs.youtrade.autotrade.client.util.excel.XlsxParserHelper.getCellString;

@Service
public class TableV2SellingGenerator
        extends AbstractXlsxGenerator
        implements ITableGenerator<List<FcdSellListGetDto>, List<FcdSellingV2PostGroupDto>> {
    private static final List<String> utilHeaders = List.of(
            "ID аккаунта", "Имя аккаунта", "youTrade-ID"
    );
    private static final List<String> mainHeaders = List.of(
            "Дата покупки", "Название"
    );
    private static final List<String> sellHeaders = List.of(
            "Закуп $", "Мин $", "Макс $", "Текущ $", "% приб."
    );
    protected static final List<String> controlHeaders = List.of(
            "Новый закуп $", "Новый мин $", "Новый макс $"
    );
    private static final List<String> flagHeaders = List.of(
            "Снять с продажи"
    );

    @Override
    public File createFile(List<FcdSellListGetDto> input) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            // Styles creation
            CellStyle utilStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
            CellStyle dateStyle = createDateStyle(wb, () -> createSideStyle(wb, YouTradeColorCodes.SINGLE));
            CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
            CellStyle sellStyle = createSideStyle(wb, YouTradeColorCodes.GROUP);
            CellStyle controlStyle = createSideStyle(wb, YouTradeColorCodes.CONTROL);

            for (var dto : input) {
                // Sheet creation
                var list = dto.getOnSellList();
                Sheet sheet = wb.createSheet(dto.getTokenName());

                // Инициализация заголовков
                int rowIdx = 0;
                int totalColumns = fillHeaderRow(sheet, rowIdx++, utilStyle, mainStyle, sellStyle, controlStyle);
                for (var item : list) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, dto, item, utilStyle, dateStyle, mainStyle, sellStyle, controlStyle);
                }
                autoSizeColumns(sheet, totalColumns);

                // Validation for TRUE/FALSE
                if (!list.isEmpty()) {
                    int lastColumnIdx = totalColumns - 1;
                    DataValidationHelper dvHelper = sheet.getDataValidationHelper();
                    DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(new String[]{"TRUE", "FALSE"});
                    CellRangeAddressList addressList = new CellRangeAddressList(
                            1, list.size(),
                            lastColumnIdx, lastColumnIdx
                    );
                    DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
                    validation.setShowErrorBox(true);
                    sheet.addValidationData(validation);
                }
            }

            File out = File.createTempFile("sell_listed_", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(out)) {
                wb.write(fos);
                return out;
            }
        }
    }

    private void fillRow(
            Row row,
            FcdSellListGetDto getDto,
            YouTradeOnSellItemMainInfoDto item,
            CellStyle utilStyle,
            CellStyle dateStyle,
            CellStyle mainStyle,
            CellStyle sellStyle,
            CellStyle flagStyle
    ) {
        int col = 0;
        col = fillUtil(col, row, getDto, item, utilStyle);
        col = fillDate(col, row, item, dateStyle);
        col = fillMain(col, row, item, mainStyle);
        col = fillSell(col, row, item, sellStyle);
        col = fillControl(col, row, flagStyle);
        fillFlag(col, row, flagStyle);
    }

    private int fillUtil(
            int rOrd,
            Row row,
            FcdSellListGetDto getDto,
            YouTradeOnSellItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                getDto.getTmTokenId(),
                item.getGivenName(),
                item.getYouTradeId()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillDate(
            int rOrd,
            Row row,
            YouTradeOnSellItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getPurchasedAt()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillMain(
            int rOrd,
            Row row,
            YouTradeOnSellItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getItemName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillSell(
            int rOrd,
            Row row,
            YouTradeOnSellItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getBuyPrice(),
                item.getItemMin(),
                item.getItemMax(),
                item.getSellPrice(),
                item.getSellProfit().toPlainString() + " %"
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillControl(
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

    private int fillFlag(
            int rOrd,
            Row row,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                "FALSE"
        );
        return setCellValues(rOrd, row, style, objects);
    }

    private int fillHeaderRow(
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
        rOrd = createHeader(rOrd, headerRow, controlHeaders, controlStyle);
        return createHeader(rOrd, headerRow, flagHeaders, controlStyle);
    }

    @Override
    public List<FcdSellingV2PostGroupDto> handleFile(File file) throws IOException {
        List<FcdSellingV2PostGroupDto> toPost = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(file))) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                String tokenName = sheet.getSheetName();
                List<FcdSellingV2PostDto> dtos = new ArrayList<>();
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    String idStr = getCellString(row.getCell(2));
                    if (idStr.isEmpty()) continue;

                    String name = getCellString(row.getCell(4));
                    if (name.isEmpty()) continue;

                    String oldBaseStr = getCellString(row.getCell(5));
                    if (oldBaseStr.isEmpty()) continue;

                    String oldMinStr = getCellString(row.getCell(6));
                    if (oldMinStr.isEmpty()) continue;

                    String oldMaxStr = getCellString(row.getCell(7));
                    if (oldMaxStr.isEmpty()) continue;

                    String marketPriceStr = getCellString(row.getCell(8));
                    if (marketPriceStr.isEmpty()) continue;

                    String newBaseStr = getCellString(row.getCell(10));
                    if (newBaseStr.isEmpty()) newBaseStr = "0";

                    String newMinStr = getCellString(row.getCell(11));
                    if (newMinStr.isEmpty()) newMinStr = "0";

                    String newMaxStr = getCellString(row.getCell(12));
                    if (newMaxStr.isEmpty()) newMaxStr = "0";

                    String flagStr = Optional.ofNullable(row.getCell(13))
                            .map(XlsxParserHelper::getCellString)
                            .orElse("FALSE");

                    if (newMinStr.equals(newMaxStr)
                        && newMinStr.equals(newBaseStr)
                        && flagStr.equals("FALSE"))
                        continue;

                    dtos.add(new FcdSellingV2PostDto(
                            idStr,
                            name,
                            marketPriceStr,
                            oldMinStr,
                            oldMaxStr,
                            oldBaseStr,
                            newMinStr,
                            newMaxStr,
                            newBaseStr,
                            flagStr
                    ));
                }
                toPost.add(new FcdSellingV2PostGroupDto(tokenName, dtos));
            }
            return toPost;
        }
    }
}
