package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.selling.stage1.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.ITableGenerator;
import cs.youtrade.autotrade.client.util.YouTradeColorCodes;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListPostDto;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeOnSellItemMainInfoDto;
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
public class TableSellingGenerator
        extends AbstractXlsxGenerator
        implements ITableGenerator<List<FcdSellListGetDto>, List<FcdSellListPostDto>> {
    private static final List<String> utilHeaders = List.of(
            "token-ID", "Имя токена", "youTrade-ID"
    );
    private static final List<String> mainHeaders = List.of(
            "Дата покупки", "Название"
    );
    private static final List<String> sellHeaders = List.of(
            "Закуп $", "Мин $", "Макс $", "Текущ $", "% приб."
    );
    private static final List<String> controlHeaders = List.of(
            "Снять с продажи"
    );

    @Override
    public File createFile(List<FcdSellListGetDto> input) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            for (var getDto : input) {
                // Styles creation
                CellStyle utilStyle = createMainStyle(wb, YouTradeColorCodes.MAIN);
                CellStyle dateStyle = createDateStyle(wb, () -> createSideStyle(wb, YouTradeColorCodes.SINGLE));
                CellStyle mainStyle = createSideStyle(wb, YouTradeColorCodes.SINGLE);
                CellStyle sellStyle = createSideStyle(wb, YouTradeColorCodes.GROUP);
                CellStyle flagStyle = createMainStyle(wb, YouTradeColorCodes.FLAG);

                // Sheet creation
                List<YouTradeOnSellItemMainInfoDto> list = getDto.getOnSellList();
                Sheet sheet = wb.createSheet(getDto.getTokenName());

                // Инициализация заголовков
                int rowIdx = 0;
                int totalColumns = fillHeaderRow(sheet, rowIdx++, utilStyle, mainStyle, sellStyle, flagStyle);
                for (YouTradeOnSellItemMainInfoDto item : list) {
                    Row row = sheet.createRow(rowIdx++);
                    fillRow(row, getDto, item, utilStyle, dateStyle, mainStyle, sellStyle, flagStyle);
                }
                autoSizeColumns(sheet, totalColumns);

                // Validation for TRUE/FALSE
                DataValidationHelper dvHelper = sheet.getDataValidationHelper();
                DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(new String[]{"TRUE", "FALSE"});
                CellRangeAddressList addressList = new CellRangeAddressList(
                        1, list.size(),
                        totalColumns, totalColumns
                );
                DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
                validation.setShowErrorBox(true);
                sheet.addValidationData(validation);
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
                item.getItemPrice(),
                item.getItemMin(),
                item.getItemMax(),
                item.getSellPrice(),
                item.getSellProfit()
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

    @Override
    public List<FcdSellListPostDto> handleFile(File file) throws IOException {
        List<FcdSellListPostDto> toPost = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(file))) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0)
                        continue;

                    String tokenId = getCellString(row.getCell(0));
                    if (tokenId.isEmpty())
                        continue;

                    String ytIdStr = getCellString(row.getCell(2));
                    if (ytIdStr.isEmpty())
                        continue;

                    String flagStr = Optional.ofNullable(row.getCell(10))
                            .map(Cell::toString)
                            .orElse("FALSE");
                    if (flagStr.equals("FALSE"))
                        continue;

                    toPost.add(new FcdSellListPostDto(tokenId, ytIdStr, flagStr));
                }
            }
            return toPost;
        }
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
        return createHeader(rOrd, headerRow, controlHeaders, controlStyle);
    }
}
