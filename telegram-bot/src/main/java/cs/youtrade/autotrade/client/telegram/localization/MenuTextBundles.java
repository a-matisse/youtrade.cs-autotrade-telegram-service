package cs.youtrade.autotrade.client.telegram.localization;

import java.util.Map;

final class MenuTextBundles {
    private MenuTextBundles() {
    }

    static final Map<String, String> BUNDLES = Map.ofEntries(
            Map.entry("AccountsAddTradeUrlMenu", "accounts.add.BuyerAdd"),
            Map.entry("AccountsChooseOption", "accounts.AccountOptions"),
            Map.entry("BalanceMarketSellAmountMenu", "accounts.money-sell.BalanceMarketSellAmount"),
            Map.entry("BalanceMarketSellMethodMenu", "accounts.money-sell.BalanceMarketSellMethod"),
            Map.entry("BalanceMarketSellProcessingMenu", "accounts.money-sell.BalanceMarketSellProcessing"),
            Map.entry("BalanceMarketSellResultMenu", "accounts.money-sell.BalanceMarketSellResult"),
            Map.entry("BalanceMarketSellReviewMenu", "accounts.money-sell.BalanceMarketSellReview"),
            Map.entry("ClassicTableMenu", "portfolio.UserTable"),
            Map.entry("DeleteRequestMenu", "parameters.ParamsDelete"),
            Map.entry("GetNewestItemsHrsMenu", "parameters.autobuy.GetNewestItems"),
            Map.entry("ItemScoringTypeMenu", "parameters.scoring.UserScoring"),
            Map.entry("MarketCsgoCollectProcessingMenu", "accounts.money-collect.MarketCsgoCollectProcessing"),
            Map.entry("MarketCsgoSendAmountMenu", "accounts.money-send.MarketCsgoSendAmount"),
            Map.entry("MarketCsgoSendConfirmMenu", "accounts.money-send.MarketCsgoSendConfirm"),
            Map.entry("MarketCsgoSendProcessingMenu", "accounts.money-send.MarketCsgoSendProcessing"),
            Map.entry("MarketCsgoSendVerifyMenu", "accounts.money-send.MarketCsgoSendVerify"),
            Map.entry("MarketType", "parameters.TradingOptions"),
            Map.entry("PageMenu", "common.Interface"),
            Map.entry("PurchaseFreezeMenu", "accounts.PurchaseFreeze"),
            Map.entry("QuickConfigGradeMenu", "parameters.QuickConfig"),
            Map.entry("RefPayoutMenu", "referral.RefPayout"),
            Map.entry("RefTransferAmountMenu", "referral.transfer.RefTransferAmount"),
            Map.entry("RefTransferConfirmMenu", "referral.transfer.RefTransferConfirm"),
            Map.entry("RefTransferProceedMenu", "referral.transfer.RefTransferProceed"),
            Map.entry("TableHistoryMode", "portfolio.history.TableHistoryPeriod"),
            Map.entry("TableHistoryPeriodMenu", "portfolio.history.TableHistoryPeriod"),
            Map.entry("TableV2RestoreAgreementMenu", "portfolio.TableV2Restore"),
            Map.entry("TerminalMenu", "common.Interface"),
            Map.entry("TokenAddValueMenu", "accounts.add.AbstractAddApi"),
            Map.entry("UserAccountsMenu", "accounts.UserAccounts"),
            Map.entry("UserAccountsMoreMenu", "accounts.UserAccountsMore"),
            Map.entry("UserAutoBuyMenu", "parameters.autobuy.UserAutoBuy"),
            Map.entry("UserAutoSellMenu", "parameters.autosell.UserAutoSell"),
            Map.entry("UserDeepParamsMenu", "parameters.UserDeepParams"),
            Map.entry("UserFollowCheckMenu", "parameters.follow.UserFollowCheck"),
            Map.entry("UserFollowMenu", "parameters.follow.UserFollow"),
            Map.entry("UserFollowOperationType", "parameters.follow.UserFollow"),
            Map.entry("UserLanguageMenu", "preferences.UserLanguage"),
            Map.entry("UserParamsMenu", "parameters.UserParams"),
            Map.entry("UserPayProceedMenu", "topup.UserPayProceed"),
            Map.entry("UserPreferencesMenu", "preferences.UserPreferences"),
            Map.entry("UserRefMenu", "referral.UserRef"),
            Map.entry("UserScoringMenu", "parameters.scoring.UserScoring"),
            Map.entry("UserStartMenu", "start.UserStart"),
            Map.entry("UserTableMenu", "portfolio.UserTable"),
            Map.entry("UserWordsMenu", "parameters.autobuy.UserWords"),
            Map.entry("WordsType", "parameters.autobuy.UserWords"),
            Map.entry("WorkerAddMaFileMenu", "accounts.add.WorkerAdd"),
            Map.entry("YTBalanceNotifyMenu", "notification.BalanceNotifications"),
            Map.entry("YTWelcomeMessageMenu", "notification.YTWelcomeMessage")
    );
}
