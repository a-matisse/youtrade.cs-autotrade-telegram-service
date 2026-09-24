package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.generator;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.sell.FcdSellHistoryFullDto;
import cs.youtrade.autotrade.client.util.autotrade.util.HistoryDateTimeFormat;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradeSoldItemMainInfoDto;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class TableSellHistoryGenerator extends AbstractTableHistoryGenerator<FcdSellHistoryFullDto, YouTradeSoldItemMainInfoDto> {
    @Override
    protected String getReportTitle() {
        return "ИСТОРИЯ ПРОДАЖ";
    }

    @Override
    public int fillUtil(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                idText(item.getTokenId()),
                idText(item.getSteamToken()),
                item.getGivenName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    protected LocalDateTime getHistoryDate(YouTradeSoldItemMainInfoDto item) {
        return HistoryDateTimeFormat.parse(item.getSoldAt());
    }

    @Override
    public int fillDate(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                HistoryDateTimeFormat.parse(item.getBoughtAt()),
                HistoryDateTimeFormat.parse(item.getSoldAt())
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillMain(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getItemName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillItem(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        List<Object> objects = Arrays.asList(
                item.getBoughtOn(),
                item.getSoldOn()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillSell(
            int rOrd,
            Row row,
            YouTradeSoldItemMainInfoDto item,
            CellStyle style
    ) {
        BigDecimal profit = BigDecimal
                .valueOf(item.getCleanSellPercent())
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        List<Object> objects = Arrays.asList(
                item.getBuyPrice(),
                item.getCleanSellPrice(),
                profit,
                BigDecimal.valueOf(item.getCleanSellPrice())
                        .subtract(BigDecimal.valueOf(item.getBuyPrice()))
                        .setScale(2, RoundingMode.HALF_UP)
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public List<String> getUtilHeaders() {
        return List.of("token-ID", "Steam аккаунт", "Имя аккаунта");
    }

    @Override
    public List<String> getMainHeaders() {
        return List.of("Дата покупки", "Дата продажи", "Название");
    }

    @Override
    public List<String> getItemHeaders() {
        return List.of("Куплено на", "Продано на");
    }

    @Override
    public List<String> getSellHeaders() {
        return List.of("Buy, $", "Sell, $", "Profit, %", "Profit, $");
    }
}
