package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortfolioTableType {
    INVENTORY(UserMenu.PORTFOLIO_V2_INVENTORY_STAGE_1),
    SELLING(UserMenu.PORTFOLIO_V2_SELLING_STAGE_1),
    WAITING(UserMenu.PORTFOLIO_WAITING),
    BUY_HISTORY(UserMenu.PORTFOLIO_HISTORY_STAGE_P_BUY),
    SELL_HISTORY(UserMenu.PORTFOLIO_HISTORY_STAGE_P_SELL);

    private final UserMenu tableState;
}
