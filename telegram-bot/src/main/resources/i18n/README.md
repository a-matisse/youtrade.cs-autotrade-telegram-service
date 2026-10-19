# Словари Telegram-бота

Подключён весь интерфейс RU/EN: меню, формы, результаты, кнопки, уведомления,
общие блоки и Excel-таблицы. Язык берётся из сохранённого выбора пользователя.

В словарях — подписи и цельные фразы с именованными аргументами ICU.
HTML, оформление, эмодзи и клавиатуры остаются в Java. Названия аккаунтов,
предметов и произвольные тексты backend не переводятся автоматически.

Всего 1287 ключей на язык, 103 пар RU/EN.
Имена файлов связаны с существующими меню и сценариями.

`BotTexts.forUser(user, "notification.YTWelcomeMessage")` читает пару
`notification/YTWelcomeMessage_ru.properties` / `_en.properties`.
Перечислять все наборы в Spring-конфигурации не требуется. Соответствия
enum кнопок наборам находятся в `MenuTextBundles`; их значки остаются в enum.

[Схема локализации и примеры оформления](../../../../../docs/telegram-localization.md).

## accounts

| Набор | RU | EN | Ключей | Состояние |
|---|---|---|---|---|
| `accounts/AccountInfo` | [RU](accounts/AccountInfo_ru.properties) | [EN](accounts/AccountInfo_en.properties) | 37 | Подключён |
| `accounts/AccountOptions` | [RU](accounts/AccountOptions_ru.properties) | [EN](accounts/AccountOptions_en.properties) | 10 | Подключён |
| `accounts/add/AbstractAddApi` | [RU](accounts/add/AbstractAddApi_ru.properties) | [EN](accounts/add/AbstractAddApi_en.properties) | 10 | Подключён |
| `accounts/add/AccountsAddChoose` | [RU](accounts/add/AccountsAddChoose_ru.properties) | [EN](accounts/add/AccountsAddChoose_en.properties) | 11 | Подключён |
| `accounts/add/BuyerAdd` | [RU](accounts/add/BuyerAdd_ru.properties) | [EN](accounts/add/BuyerAdd_en.properties) | 13 | Подключён |
| `accounts/add/SellerAdd` | [RU](accounts/add/SellerAdd_ru.properties) | [EN](accounts/add/SellerAdd_en.properties) | 1 | Подключён |
| `accounts/add/WorkerAdd` | [RU](accounts/add/WorkerAdd_ru.properties) | [EN](accounts/add/WorkerAdd_en.properties) | 21 | Подключён |
| `accounts/money-collect/MarketCsgoCollect` | [RU](accounts/money-collect/MarketCsgoCollect_ru.properties) | [EN](accounts/money-collect/MarketCsgoCollect_en.properties) | 11 | Подключён |
| `accounts/money-collect/MarketCsgoCollectPassword` | [RU](accounts/money-collect/MarketCsgoCollectPassword_ru.properties) | [EN](accounts/money-collect/MarketCsgoCollectPassword_en.properties) | 8 | Подключён |
| `accounts/money-collect/MarketCsgoCollectProcessing` | [RU](accounts/money-collect/MarketCsgoCollectProcessing_ru.properties) | [EN](accounts/money-collect/MarketCsgoCollectProcessing_en.properties) | 7 | Подключён |
| `accounts/money-collect/MarketCsgoCollectResult` | [RU](accounts/money-collect/MarketCsgoCollectResult_ru.properties) | [EN](accounts/money-collect/MarketCsgoCollectResult_en.properties) | 17 | Подключён |
| `accounts/money-sell/BalanceMarketSell` | [RU](accounts/money-sell/BalanceMarketSell_ru.properties) | [EN](accounts/money-sell/BalanceMarketSell_en.properties) | 7 | Подключён |
| `accounts/money-sell/BalanceMarketSellAmount` | [RU](accounts/money-sell/BalanceMarketSellAmount_ru.properties) | [EN](accounts/money-sell/BalanceMarketSellAmount_en.properties) | 7 | Подключён |
| `accounts/money-sell/BalanceMarketSellCommon` | [RU](accounts/money-sell/BalanceMarketSellCommon_ru.properties) | [EN](accounts/money-sell/BalanceMarketSellCommon_en.properties) | 12 | Подключён |
| `accounts/money-sell/BalanceMarketSellMethod` | [RU](accounts/money-sell/BalanceMarketSellMethod_ru.properties) | [EN](accounts/money-sell/BalanceMarketSellMethod_en.properties) | 2 | Подключён |
| `accounts/money-sell/BalanceMarketSellPassword` | [RU](accounts/money-sell/BalanceMarketSellPassword_ru.properties) | [EN](accounts/money-sell/BalanceMarketSellPassword_en.properties) | 4 | Подключён |
| `accounts/money-sell/BalanceMarketSellProcessing` | [RU](accounts/money-sell/BalanceMarketSellProcessing_ru.properties) | [EN](accounts/money-sell/BalanceMarketSellProcessing_en.properties) | 2 | Подключён |
| `accounts/money-sell/BalanceMarketSellResult` | [RU](accounts/money-sell/BalanceMarketSellResult_ru.properties) | [EN](accounts/money-sell/BalanceMarketSellResult_en.properties) | 67 | Подключён |
| `accounts/money-sell/BalanceMarketSellReview` | [RU](accounts/money-sell/BalanceMarketSellReview_ru.properties) | [EN](accounts/money-sell/BalanceMarketSellReview_en.properties) | 18 | Подключён |
| `accounts/money-sell/BalanceMarketSellWallet` | [RU](accounts/money-sell/BalanceMarketSellWallet_ru.properties) | [EN](accounts/money-sell/BalanceMarketSellWallet_en.properties) | 2 | Подключён |
| `accounts/money-send/MarketCsgoSend` | [RU](accounts/money-send/MarketCsgoSend_ru.properties) | [EN](accounts/money-send/MarketCsgoSend_en.properties) | 9 | Подключён |
| `accounts/money-send/MarketCsgoSendAmount` | [RU](accounts/money-send/MarketCsgoSendAmount_ru.properties) | [EN](accounts/money-send/MarketCsgoSendAmount_en.properties) | 9 | Подключён |
| `accounts/money-send/MarketCsgoSendCommon` | [RU](accounts/money-send/MarketCsgoSendCommon_ru.properties) | [EN](accounts/money-send/MarketCsgoSendCommon_en.properties) | 10 | Подключён |
| `accounts/money-send/MarketCsgoSendConfirm` | [RU](accounts/money-send/MarketCsgoSendConfirm_ru.properties) | [EN](accounts/money-send/MarketCsgoSendConfirm_en.properties) | 4 | Подключён |
| `accounts/money-send/MarketCsgoSendDestination` | [RU](accounts/money-send/MarketCsgoSendDestination_ru.properties) | [EN](accounts/money-send/MarketCsgoSendDestination_en.properties) | 3 | Подключён |
| `accounts/money-send/MarketCsgoSendPassword` | [RU](accounts/money-send/MarketCsgoSendPassword_ru.properties) | [EN](accounts/money-send/MarketCsgoSendPassword_en.properties) | 3 | Подключён |
| `accounts/money-send/MarketCsgoSendProcessing` | [RU](accounts/money-send/MarketCsgoSendProcessing_ru.properties) | [EN](accounts/money-send/MarketCsgoSendProcessing_en.properties) | 2 | Подключён |
| `accounts/money-send/MarketCsgoSendResult` | [RU](accounts/money-send/MarketCsgoSendResult_ru.properties) | [EN](accounts/money-send/MarketCsgoSendResult_en.properties) | 12 | Подключён |
| `accounts/money-send/MarketCsgoSendVerify` | [RU](accounts/money-send/MarketCsgoSendVerify_ru.properties) | [EN](accounts/money-send/MarketCsgoSendVerify_en.properties) | 7 | Подключён |
| `accounts/MoneyCommon` | [RU](accounts/MoneyCommon_ru.properties) | [EN](accounts/MoneyCommon_en.properties) | 3 | Подключён |
| `accounts/PurchaseFreeze` | [RU](accounts/PurchaseFreeze_ru.properties) | [EN](accounts/PurchaseFreeze_en.properties) | 30 | Подключён |
| `accounts/TokenDelete` | [RU](accounts/TokenDelete_ru.properties) | [EN](accounts/TokenDelete_en.properties) | 22 | Подключён |
| `accounts/TokenRename` | [RU](accounts/TokenRename_ru.properties) | [EN](accounts/TokenRename_en.properties) | 11 | Подключён |
| `accounts/TokenTransfer` | [RU](accounts/TokenTransfer_ru.properties) | [EN](accounts/TokenTransfer_en.properties) | 14 | Подключён |
| `accounts/UserAccounts` | [RU](accounts/UserAccounts_ru.properties) | [EN](accounts/UserAccounts_en.properties) | 11 | Подключён |
| `accounts/UserAccountsMore` | [RU](accounts/UserAccountsMore_ru.properties) | [EN](accounts/UserAccountsMore_en.properties) | 9 | Подключён |

## common

| Набор | RU | EN | Ключей | Состояние |
|---|---|---|---|---|
| `common/Commands` | [RU](common/Commands_ru.properties) | [EN](common/Commands_en.properties) | 13 | Подключён |
| `common/Interface` | [RU](common/Interface_ru.properties) | [EN](common/Interface_en.properties) | 10 | Подключён |

## notification

| Набор | RU | EN | Ключей | Состояние |
|---|---|---|---|---|
| `notification/AccessNotifications` | [RU](notification/AccessNotifications_ru.properties) | [EN](notification/AccessNotifications_en.properties) | 15 | Подключён |
| `notification/BalanceNotifications` | [RU](notification/BalanceNotifications_ru.properties) | [EN](notification/BalanceNotifications_en.properties) | 24 | Подключён |
| `notification/BargainNotifications` | [RU](notification/BargainNotifications_ru.properties) | [EN](notification/BargainNotifications_en.properties) | 16 | Подключён |
| `notification/BuyNotifications` | [RU](notification/BuyNotifications_ru.properties) | [EN](notification/BuyNotifications_en.properties) | 18 | Подключён |
| `notification/InventoryNotifications` | [RU](notification/InventoryNotifications_ru.properties) | [EN](notification/InventoryNotifications_en.properties) | 8 | Подключён |
| `notification/SellNotifications` | [RU](notification/SellNotifications_ru.properties) | [EN](notification/SellNotifications_en.properties) | 11 | Подключён |
| `notification/SkinNotification` | [RU](notification/SkinNotification_ru.properties) | [EN](notification/SkinNotification_en.properties) | 2 | Подключён |
| `notification/YTMaFileDeleted` | [RU](notification/YTMaFileDeleted_ru.properties) | [EN](notification/YTMaFileDeleted_en.properties) | 2 | Подключён |
| `notification/YTPayment` | [RU](notification/YTPayment_ru.properties) | [EN](notification/YTPayment_en.properties) | 8 | Подключён |
| `notification/YTWelcomeMessage` | [RU](notification/YTWelcomeMessage_ru.properties) | [EN](notification/YTWelcomeMessage_en.properties) | 9 | Подключён |

## parameters

| Набор | RU | EN | Ключей | Состояние |
|---|---|---|---|---|
| `parameters/autobuy/AutoBuyModes` | [RU](parameters/autobuy/AutoBuyModes_ru.properties) | [EN](parameters/autobuy/AutoBuyModes_en.properties) | 8 | Подключён |
| `parameters/autobuy/AutoBuyUpdate` | [RU](parameters/autobuy/AutoBuyUpdate_ru.properties) | [EN](parameters/autobuy/AutoBuyUpdate_en.properties) | 5 | Подключён |
| `parameters/autobuy/GetNewestItems` | [RU](parameters/autobuy/GetNewestItems_ru.properties) | [EN](parameters/autobuy/GetNewestItems_en.properties) | 15 | Подключён |
| `parameters/autobuy/UserAutoBuy` | [RU](parameters/autobuy/UserAutoBuy_ru.properties) | [EN](parameters/autobuy/UserAutoBuy_en.properties) | 27 | Подключён |
| `parameters/autobuy/UserWords` | [RU](parameters/autobuy/UserWords_ru.properties) | [EN](parameters/autobuy/UserWords_en.properties) | 22 | Подключён |
| `parameters/autosell/AutoSellUpdate` | [RU](parameters/autosell/AutoSellUpdate_ru.properties) | [EN](parameters/autosell/AutoSellUpdate_en.properties) | 5 | Подключён |
| `parameters/autosell/UserAutoSell` | [RU](parameters/autosell/UserAutoSell_ru.properties) | [EN](parameters/autosell/UserAutoSell_en.properties) | 15 | Подключён |
| `parameters/follow/AbstractPco` | [RU](parameters/follow/AbstractPco_ru.properties) | [EN](parameters/follow/AbstractPco_en.properties) | 2 | Подключён |
| `parameters/follow/FollowRequest` | [RU](parameters/follow/FollowRequest_ru.properties) | [EN](parameters/follow/FollowRequest_en.properties) | 12 | Подключён |
| `parameters/follow/ParamsCopyOptions` | [RU](parameters/follow/ParamsCopyOptions_ru.properties) | [EN](parameters/follow/ParamsCopyOptions_en.properties) | 16 | Подключён |
| `parameters/follow/UserFollow` | [RU](parameters/follow/UserFollow_ru.properties) | [EN](parameters/follow/UserFollow_en.properties) | 9 | Подключён |
| `parameters/follow/UserFollowCheck` | [RU](parameters/follow/UserFollowCheck_ru.properties) | [EN](parameters/follow/UserFollowCheck_en.properties) | 11 | Подключён |
| `parameters/ParamsCreate` | [RU](parameters/ParamsCreate_ru.properties) | [EN](parameters/ParamsCreate_en.properties) | 5 | Подключён |
| `parameters/ParamsDelete` | [RU](parameters/ParamsDelete_ru.properties) | [EN](parameters/ParamsDelete_en.properties) | 8 | Подключён |
| `parameters/ParamsList` | [RU](parameters/ParamsList_ru.properties) | [EN](parameters/ParamsList_en.properties) | 1 | Подключён |
| `parameters/ParamsRename` | [RU](parameters/ParamsRename_ru.properties) | [EN](parameters/ParamsRename_en.properties) | 9 | Подключён |
| `parameters/ParamsSwitch` | [RU](parameters/ParamsSwitch_ru.properties) | [EN](parameters/ParamsSwitch_en.properties) | 4 | Подключён |
| `parameters/QuickConfig` | [RU](parameters/QuickConfig_ru.properties) | [EN](parameters/QuickConfig_en.properties) | 25 | Подключён |
| `parameters/scoring/UserScoring` | [RU](parameters/scoring/UserScoring_ru.properties) | [EN](parameters/scoring/UserScoring_en.properties) | 33 | Подключён |
| `parameters/scoring/YdpField` | [RU](parameters/scoring/YdpField_ru.properties) | [EN](parameters/scoring/YdpField_en.properties) | 8 | Подключён |
| `parameters/TdpField` | [RU](parameters/TdpField_ru.properties) | [EN](parameters/TdpField_en.properties) | 34 | Подключён |
| `parameters/TradingOptions` | [RU](parameters/TradingOptions_ru.properties) | [EN](parameters/TradingOptions_en.properties) | 21 | Подключён |
| `parameters/UserDeepParams` | [RU](parameters/UserDeepParams_ru.properties) | [EN](parameters/UserDeepParams_en.properties) | 23 | Подключён |
| `parameters/UserParams` | [RU](parameters/UserParams_ru.properties) | [EN](parameters/UserParams_en.properties) | 13 | Подключён |

## portfolio

| Набор | RU | EN | Ключей | Состояние |
|---|---|---|---|---|
| `portfolio/history/TableHistoryPeriod` | [RU](portfolio/history/TableHistoryPeriod_ru.properties) | [EN](portfolio/history/TableHistoryPeriod_en.properties) | 27 | Подключён |
| `portfolio/history/TableHistoryResult` | [RU](portfolio/history/TableHistoryResult_ru.properties) | [EN](portfolio/history/TableHistoryResult_en.properties) | 10 | Подключён |
| `portfolio/PortfolioEmpty` | [RU](portfolio/PortfolioEmpty_ru.properties) | [EN](portfolio/PortfolioEmpty_en.properties) | 12 | Подключён |
| `portfolio/TableV2Inventory` | [RU](portfolio/TableV2Inventory_ru.properties) | [EN](portfolio/TableV2Inventory_en.properties) | 11 | Подключён |
| `portfolio/TableV2Restore` | [RU](portfolio/TableV2Restore_ru.properties) | [EN](portfolio/TableV2Restore_en.properties) | 9 | Подключён |
| `portfolio/TableV2Selling` | [RU](portfolio/TableV2Selling_ru.properties) | [EN](portfolio/TableV2Selling_en.properties) | 12 | Подключён |
| `portfolio/TableWaiting` | [RU](portfolio/TableWaiting_ru.properties) | [EN](portfolio/TableWaiting_en.properties) | 6 | Подключён |
| `portfolio/UserTable` | [RU](portfolio/UserTable_ru.properties) | [EN](portfolio/UserTable_en.properties) | 14 | Подключён |

## preferences

| Набор | RU | EN | Ключей | Состояние |
|---|---|---|---|---|
| `preferences/AccountPassword` | [RU](preferences/AccountPassword_ru.properties) | [EN](preferences/AccountPassword_en.properties) | 21 | Подключён |
| `preferences/UserLanguage` | [RU](preferences/UserLanguage_ru.properties) | [EN](preferences/UserLanguage_en.properties) | 8 | Подключён |
| `preferences/UserPreferences` | [RU](preferences/UserPreferences_ru.properties) | [EN](preferences/UserPreferences_en.properties) | 19 | Подключён |

## referral

| Набор | RU | EN | Ключей | Состояние |
|---|---|---|---|---|
| `referral/RefInvitation` | [RU](referral/RefInvitation_ru.properties) | [EN](referral/RefInvitation_en.properties) | 7 | Подключён |
| `referral/RefPayout` | [RU](referral/RefPayout_ru.properties) | [EN](referral/RefPayout_en.properties) | 11 | Подключён |
| `referral/transfer/RefTransferAmount` | [RU](referral/transfer/RefTransferAmount_ru.properties) | [EN](referral/transfer/RefTransferAmount_en.properties) | 10 | Подключён |
| `referral/transfer/RefTransferConfirm` | [RU](referral/transfer/RefTransferConfirm_ru.properties) | [EN](referral/transfer/RefTransferConfirm_en.properties) | 10 | Подключён |
| `referral/transfer/RefTransferProceed` | [RU](referral/transfer/RefTransferProceed_ru.properties) | [EN](referral/transfer/RefTransferProceed_en.properties) | 12 | Подключён |
| `referral/UserRef` | [RU](referral/UserRef_ru.properties) | [EN](referral/UserRef_en.properties) | 19 | Подключён |

## start

| Набор | RU | EN | Ключей | Состояние |
|---|---|---|---|---|
| `start/UserGetPrice` | [RU](start/UserGetPrice_ru.properties) | [EN](start/UserGetPrice_en.properties) | 9 | Подключён |
| `start/UserStart` | [RU](start/UserStart_ru.properties) | [EN](start/UserStart_en.properties) | 26 | Подключён |

## summary

| Набор | RU | EN | Ключей | Состояние |
|---|---|---|---|---|
| `summary/AccountsSummary` | [RU](summary/AccountsSummary_ru.properties) | [EN](summary/AccountsSummary_en.properties) | 10 | Подключён |
| `summary/ParamsSummary` | [RU](summary/ParamsSummary_ru.properties) | [EN](summary/ParamsSummary_en.properties) | 25 | Подключён |

## table

| Набор | RU | EN | Ключей | Состояние |
|---|---|---|---|---|
| `table/AbstractXlsx` | [RU](table/AbstractXlsx_ru.properties) | [EN](table/AbstractXlsx_en.properties) | 1 | Подключён |
| `table/history/AbstractTableHistory` | [RU](table/history/AbstractTableHistory_ru.properties) | [EN](table/history/AbstractTableHistory_en.properties) | 2 | Подключён |
| `table/history/TableBuyHistory` | [RU](table/history/TableBuyHistory_ru.properties) | [EN](table/history/TableBuyHistory_en.properties) | 11 | Подключён |
| `table/history/TableSellHistory` | [RU](table/history/TableSellHistory_ru.properties) | [EN](table/history/TableSellHistory_en.properties) | 12 | Подключён |
| `table/NewestItemsXlsx` | [RU](table/NewestItemsXlsx_ru.properties) | [EN](table/NewestItemsXlsx_en.properties) | 20 | Подключён |
| `table/TableV2Inventory` | [RU](table/TableV2Inventory_ru.properties) | [EN](table/TableV2Inventory_en.properties) | 5 | Подключён |
| `table/TableV2Selling` | [RU](table/TableV2Selling_ru.properties) | [EN](table/TableV2Selling_en.properties) | 20 | Подключён |
| `table/TableWaiting` | [RU](table/TableWaiting_ru.properties) | [EN](table/TableWaiting_en.properties) | 15 | Подключён |

## topup

| Набор | RU | EN | Ключей | Состояние |
|---|---|---|---|---|
| `topup/UserPayAmount` | [RU](topup/UserPayAmount_ru.properties) | [EN](topup/UserPayAmount_en.properties) | 8 | Подключён |
| `topup/UserPayProceed` | [RU](topup/UserPayProceed_ru.properties) | [EN](topup/UserPayProceed_en.properties) | 12 | Подключён |
