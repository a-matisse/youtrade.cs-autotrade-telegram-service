package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.generator;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.buy.FcdBuyHistoryFullDto;
import cs.youtrade.autotrade.client.util.autotrade.util.YouTradePurchasedHistoryDto;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TableBuyHistoryGenerator extends AbstractTableHistoryGenerator<FcdBuyHistoryFullDto, YouTradePurchasedHistoryDto> {
    @Override
    public int fillUtil(int rOrd, Row row, YouTradePurchasedHistoryDto item, CellStyle style) {
        List<Object> objects = Arrays.asList(
                item.getTokenId(),
                item.getSteamToken(),
                item.getGivenName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillDate(int rOrd, Row row, YouTradePurchasedHistoryDto item, CellStyle style) {
        List<Object> objects = Arrays.asList(
                item.getBoughtAt()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillMain(int rOrd, Row row, YouTradePurchasedHistoryDto item, CellStyle style) {
        List<Object> objects = Arrays.asList(
                item.getItemName()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillItem(int rOrd, Row row, YouTradePurchasedHistoryDto item, CellStyle style) {
        List<Object> objects = Arrays.asList(
                item.getBoughtOn()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public int fillSell(int rOrd, Row row, YouTradePurchasedHistoryDto item, CellStyle style) {
        List<Object> objects = Arrays.asList(
                item.getBuyPrice()
        );
        return setCellValues(rOrd, row, style, objects);
    }

    @Override
    public List<String> getUtilHeaders() {
        return List.of("token-ID", "Steam аккаунт", "Имя аккаунта");
    }

    @Override
    public List<String> getMainHeaders() {
        return List.of("Дата покупки", "Название");
    }

    @Override
    public List<String> getItemHeaders() {
        return List.of("Куплено на");
    }

    @Override
    public List<String> getSellHeaders() {
        return List.of("Куп. $");
    }
}
