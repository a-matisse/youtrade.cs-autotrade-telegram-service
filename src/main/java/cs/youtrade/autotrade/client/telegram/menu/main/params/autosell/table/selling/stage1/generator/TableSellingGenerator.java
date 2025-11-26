package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.selling.stage1.generator;

import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.ITableGenerator;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListPostDto;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeOnSellItemMainInfoDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cs.youtrade.autotrade.client.util.XlsxParserHelper.getCellString;

@Service
public class TableSellingGenerator implements ITableGenerator<List<FcdSellListGetDto>, List<FcdSellListPostDto>> {
    @Override
    public File createFile(List<FcdSellListGetDto> input) throws IOException {
        Workbook wb = new XSSFWorkbook();
        CellStyle flagStyle = wb.createCellStyle();
        flagStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        flagStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (var getDto : input) {
            String token = getDto.getTokenName();
            List<YouTradeOnSellItemMainInfoDto> list = getDto.getOnSellList();
            Sheet sheet = wb.createSheet(token);
            // Header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("tokenId");
            header.createCell(1).setCellValue("givenName");
            header.createCell(2).setCellValue("youTradeId");
            header.createCell(3).setCellValue("marketHashName");
            header.createCell(4).setCellValue("itemPrice");
            header.createCell(5).setCellValue("itemMin");
            header.createCell(6).setCellValue("itemMax");
            header.createCell(7).setCellValue("remove");
            header.getCell(7).setCellStyle(flagStyle);

            int rowIdx = 1;
            for (YouTradeOnSellItemMainInfoDto dto : list) {
                Row row = sheet.createRow(rowIdx++);

                row
                        .createCell(0)
                        .setCellValue(getDto.getTmTokenId());
                row
                        .createCell(1)
                        .setCellValue(dto.getGivenName());
                row
                        .createCell(2)
                        .setCellValue(dto.getYouTradeId());
                row
                        .createCell(3)
                        .setCellValue(dto.getItemName());
                row
                        .createCell(4)
                        .setCellValue(dto.getItemPrice());
                row
                        .createCell(5)
                        .setCellValue(dto.getItemMin());
                row
                        .createCell(6)
                        .setCellValue(dto.getItemMax());

                Cell flagCell = row
                        .createCell(7);

                flagCell.setCellValue("FALSE");
                flagCell.setCellStyle(flagStyle);
            }
            // Validation for TRUE/FALSE
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(new String[]{"TRUE", "FALSE"});
            CellRangeAddressList addressList = new CellRangeAddressList(
                    1, list.size(),
                    7, 7
            );
            DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);

            for (int col = 0; col <= 6; col++) sheet.autoSizeColumn(col);
        }
        File out = File.createTempFile("sell_listed_", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(out)) {
            wb.write(fos);
        }
        wb.close();
        return out;
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

                    String flagStr = Optional.ofNullable(row.getCell(7))
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
}
