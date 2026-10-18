package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import lombok.Data;

@Data
public class PortfolioTableData<D> {
    private PortfolioTableEmptyReason emptyReason;
    private PortfolioTableType type;
    private D data;
}
