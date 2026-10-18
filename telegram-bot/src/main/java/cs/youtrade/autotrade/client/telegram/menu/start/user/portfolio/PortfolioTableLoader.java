package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.buy.FcdBuyHistoryFullDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.sell.FcdSellHistoryFullDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.list.FcdSellListGetFullDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.v2.inventory.FcdInvV2GetDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait.FcdSellWaitFullDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.v2.SellV2InventoryEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.v2.SellV2SellingEndpoint;
import cs.youtrade.ytrest.RestAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class PortfolioTableLoader {
    private final PortfolioTableRegistry registry;
    private final SellV2InventoryEndpoint inventoryEndpoint;
    private final SellV2SellingEndpoint sellingEndpoint;
    private final SellDefaultEndpoint sellEndpoint;

    public UserMenu inventory(UserData user) {
        var data = new PortfolioTableData<List<FcdInvV2GetDto>>();
        data.setType(PortfolioTableType.INVENTORY);
        var response = response(inventoryEndpoint.getUploadedItems(user.getChatId()), () ->
                new FcdDefaultDto<>(List.of()), data);
        List<FcdInvV2GetDto> content = response.getData() == null
                ? List.of()
                : response.getData();
        data.setData(content);
        boolean hasItems = content.stream().anyMatch(dto ->
                dto != null && dto.getItems() != null && !dto.getItems().isEmpty());
        return store(user, data, hasItems);
    }

    public UserMenu selling(UserData user) {
        var data = new PortfolioTableData<FcdSellListGetFullDto>();
        data.setType(PortfolioTableType.SELLING);
        var content = response(sellingEndpoint.getSelling(user.getChatId()), FcdSellListGetFullDto::new, data);
        data.setData(content);
        return store(user, data, content.hasItems());
    }

    public UserMenu waiting(UserData user) {
        var data = new PortfolioTableData<FcdSellWaitFullDto>();
        data.setType(PortfolioTableType.WAITING);
        var content = response(sellEndpoint.getSellWaiting(user.getChatId()), FcdSellWaitFullDto::new, data);
        data.setData(content);
        return store(user, data, content.hasItems());
    }

    public UserMenu buyHistory(UserData user, int days) {
        var data = new PortfolioTableData<FcdBuyHistoryFullDto>();
        data.setType(PortfolioTableType.BUY_HISTORY);
        var content = response(sellEndpoint.getBuyHistory(user.getChatId(), days), FcdBuyHistoryFullDto::new, data);
        data.setData(content);
        return store(user, data, content.hasItems());
    }

    public UserMenu sellHistory(UserData user, int days) {
        var data = new PortfolioTableData<FcdSellHistoryFullDto>();
        data.setType(PortfolioTableType.SELL_HISTORY);
        var content = response(sellEndpoint.getSellHistory(user.getChatId(), days), FcdSellHistoryFullDto::new, data);
        data.setData(content);
        return store(user, data, content.hasItems());
    }

    private <T extends AbstractFcdDto, D> T response(
            RestAnswer<T> answer,
            Supplier<T> empty,
            PortfolioTableData<D> data
    ) {
        if (answer.getStatus() == 204) return empty.get();
        if (answer.getStatus() < 200 || answer.getStatus() >= 300)
            throw new IllegalStateException("Couldn't load portfolio table: HTTP " + answer.getStatus());
        var content = answer.getResponse();
        if (content != null && !content.isResult()) {
            var reason = emptyReason(data.getType(), content.getCause());
            if (reason != null) {
                data.setEmptyReason(reason);
                return empty.get();
            }
        }
        if (content == null || !content.isResult())
            throw new IllegalStateException("Couldn't load portfolio table: "
                    + (content == null ? "empty response" : content.getCause()));
        return content;
    }

    private PortfolioTableEmptyReason emptyReason(PortfolioTableType type, String cause) {
        return switch (type) {
            case INVENTORY, SELLING -> "#2: Не найдены токены продажи".equals(cause)
                    ? PortfolioTableEmptyReason.NO_SELL_ACCOUNTS : null;
            case BUY_HISTORY -> "#1: Нет покупок за указанный период".equals(cause)
                    ? PortfolioTableEmptyReason.NO_ITEMS : null;
            case SELL_HISTORY -> "#2: Нет продаж за указанный период".equals(cause)
                    ? PortfolioTableEmptyReason.NO_ITEMS : null;
            case WAITING -> null;
        };
    }

    private <D> UserMenu store(
            UserData user,
            PortfolioTableData<D> data,
            boolean hasItems
    ) {
        if (!hasItems && data.getEmptyReason() == null)
            data.setEmptyReason(PortfolioTableEmptyReason.NO_ITEMS);
        registry.get(user);
        registry.getOrCreate(user, () -> data);
        return hasItems
                ? data.getType().getTableState()
                : UserMenu.PORTFOLIO_EMPTY;
    }
}
