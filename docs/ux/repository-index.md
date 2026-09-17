# Static repository and interface index

Snapshot: 2026-09-17. Regenerate: `powershell -File docs/ux/build-index.ps1`.

Static declarations only; state references include error paths and self references, not just navigation. No live bot or backend calls.

Java files: 420; UserMenu states: 107; explicit supportedState handlers: 105.

| State | Explicit handler |
|---|---|
| `START` | [UserStartState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/UserStartState.java) |
| `GET_PRICE` | [UserGetPriceState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/getprice/UserGetPriceState.java) |
| `TOP_UP_STAGE_1` | [UserPayAmountState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/topup/stage1/UserPayAmountState.java) |
| `TOP_UP_STAGE_P` | [UserPayProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/topup/stagep/UserPayProceedState.java) |
| `USER` | [UserParamsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/UserParamsState.java) |
| `USER_QUICK_CONFIG_INIT_STAGE_1` | [ConfigCreateAutoBuyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stage1/ConfigCreateAutoBuyState.java) |
| `USER_QUICK_CONFIG_INIT_STAGE_2` | [ConfigCreateAutoSellState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stage2/ConfigCreateAutoSellState.java) |
| `USER_QUICK_CONFIG_INIT_STAGE_3` | [ConfigCreateBankSizeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stage3/ConfigCreateBankSizeState.java) |
| `USER_QUICK_CONFIG_INIT_STAGE_P` | [ConfigCreateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stagep/ConfigCreateProceedState.java) |
| `USER_QUICK_CONFIG_DISABLE` | [QuickConfigDisableState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/disable/QuickConfigDisableState.java) |
| `PORTFOLIO` | [UserTableState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/UserTableState.java) |
| `PORTFOLIO_WAITING` | [TableWaitingState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/waiting/TableWaitingState.java) |
| `PORTFOLIO_HISTORY_STAGE_CHOOSE` | [TableHistoryModeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/choose/TableHistoryModeState.java) |
| `PORTFOLIO_HISTORY_STAGE_1` | [TableHistoryPeriodState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stage1/TableHistoryPeriodState.java) |
| `PORTFOLIO_HISTORY_STAGE_P_BUY` | [TableBuyHistoryProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stagep/TableBuyHistoryProceedState.java) |
| `PORTFOLIO_HISTORY_STAGE_P_SELL` | [TableSellHistoryProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stagep/TableSellHistoryProceedState.java) |
| `PORTFOLIO_V2_SELLING_STAGE_1` | [TableV2SellingListState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/selling/stage1/TableV2SellingListState.java) |
| `PORTFOLIO_V2_SELLING_STAGE_P` | [TableV2SellingProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/selling/stagep/TableV2SellingProceedState.java) |
| `PORTFOLIO_V2_INVENTORY_STAGE_1` | [TableV2InventoryListState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/inventory/stage1/TableV2InventoryListState.java) |
| `PORTFOLIO_V2_INVENTORY_STAGE_P` | [TableV2InventoryProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/inventory/stagep/TableV2InventoryProceedState.java) |
| `PORTFOLIO_V2_RESTORE_STAGE_1` | [TableV2RestoreAgreementState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/restore/stage1/TableV2RestoreAgreementState.java) |
| `PORTFOLIO_V2_RESTORE_STAGE_P` | [TableV2RestoreProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/restore/stagep/TableV2RestoreProceedState.java) |
| `ACCOUNTS` | [UserAccountsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/UserAccountsState.java) |
| `ACCOUNTS_GET` | Not found in this repository |
| `ACCOUNTS_ADD_STAGE_CHOOSE` | [AccountsAddChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/choose/AccountsAddChooseState.java) |
| `ACCOUNTS_ADD_STAGE_1_BUYER` | [BuyerAddApiState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/buyer/stage1/BuyerAddApiState.java) |
| `ACCOUNTS_ADD_STAGE_1_SELLER` | [SellerAddApiState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/seller/stage1/SellerAddApiState.java) |
| `ACCOUNTS_ADD_STAGE_1_WORKER` | [WorkerAddLoginState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stage1/WorkerAddLoginState.java) |
| `ACCOUNTS_ADD_STAGE_2_BUYER` | [AccountsAddTradeUrlState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/buyer/stage2/AccountsAddTradeUrlState.java) |
| `ACCOUNTS_ADD_STAGE_2_WORKER` | [WorkerAddPasswordState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stage2/WorkerAddPasswordState.java) |
| `ACCOUNTS_ADD_STAGE_3_WORKER` | [WorkerAddMaFileState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stage3/WorkerAddMaFileState.java) |
| `ACCOUNTS_ADD_STAGE_P_BUYER` | [BuyerAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/buyer/stagep/BuyerAddProceedState.java) |
| `ACCOUNTS_ADD_STAGE_P_SELLER` | [SellerAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/seller/stagep/SellerAddProceedState.java) |
| `ACCOUNTS_ADD_STAGE_P_WORKER` | [WorkerAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stagep/WorkerAddProceedState.java) |
| `ACCOUNTS_RENAME_STAGE_1` | [TokenRenameIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/rename/stage1/TokenRenameIdState.java) |
| `ACCOUNTS_RENAME_STAGE_2` | [TokenRenameValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/rename/stage2/TokenRenameValueState.java) |
| `ACCOUNTS_RENAME_STAGE_P` | [TokenRenameProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/rename/stagep/TokenRenameProceedState.java) |
| `ACCOUNTS_REMOVE_STAGE_CHOOSE` | [TokenDeleteChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/delete/choose/TokenDeleteChooseState.java) |
| `ACCOUNTS_REMOVE_STAGE_1` | [TokenDeleteIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/delete/stage1/TokenDeleteIdState.java) |
| `ACCOUNTS_REMOVE_STAGE_P` | [TokenDeleteProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/delete/stagep/TokenDeleteProceedState.java) |
| `ACCOUNTS_TRANSFER_STAGE_1` | [TokenTransferChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/transfer/stage1/TokenTransferChooseState.java) |
| `ACCOUNTS_TRANSFER_STAGE_2` | [TokenTransferParamsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/transfer/stage2/TokenTransferParamsState.java) |
| `ACCOUNTS_TRANSFER_STAGE_P` | [TokenTransferProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/transfer/stagep/TokenTransferProceedState.java) |
| `PARAMS` | [UserDeepParamsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/UserDeepParamsState.java) |
| `PARAMS_RENAME_STAGE_1` | [ParamsRenameIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/rename/stage1/ParamsRenameIdState.java) |
| `PARAMS_RENAME_STAGE_2` | [ParamsRenameValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/rename/stage2/ParamsRenameValueState.java) |
| `PARAMS_RENAME_STAGE_P` | [ParamsRenameProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/rename/stagep/ParamsRenameProceedState.java) |
| `PARAMS_LIST` | [ParamsListState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/list/ParamsListState.java) |
| `PARAMS_SWITCH_STAGE_1` | [ParamsSwitchIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/pswitch/stage1/ParamsSwitchIdState.java) |
| `PARAMS_SWITCH_STAGE_P` | [ParamsSwitchProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/pswitch/stagep/ParamsSwitchProceedState.java) |
| `PARAMS_CREATE_STAGE_1` | [CreateSourceState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/stage1/CreateSourceState.java) |
| `PARAMS_CREATE_STAGE_2` | [CreateDestinationState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/stage2/CreateDestinationState.java) |
| `PARAMS_CREATE_STAGE_P` | [CreateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/stagep/CreateProceedState.java) |
| `PARAMS_DELETE_STAGE_1` | [DeleteRequestState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/delete/stage1/DeleteRequestState.java) |
| `PARAMS_DELETE_STAGE_P` | [DeleteProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/delete/stagep/DeleteProceedState.java) |
| `AUTOBUY` | [UserAutoBuyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/UserAutoBuyState.java) |
| `AUTOBUY_UPDATE_FIELD_STAGE_1` | [AutoBuyUpdateFieldState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/update/stage1/AutoBuyUpdateFieldState.java) |
| `AUTOBUY_UPDATE_FIELD_STAGE_2` | [AutoBuyUpdateValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/update/stage2/AutoBuyUpdateValueState.java) |
| `AUTOBUY_UPDATE_FIELD_STAGE_P` | [AutoBuyUpdateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/update/stagep/AutoBuyUpdateProceedState.java) |
| `AUTOBUY_SWITCH_FUNCTION_TYPE` | [SwitchFunctionState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/abswitch/function/SwitchFunctionState.java) |
| `AUTOBUY_SWITCH_DUPLICATE_MODE` | [SwitchDuplicateState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/abswitch/duplicate/SwitchDuplicateState.java) |
| `AUTOBUY_GET_NEWEST_ITEMS_STAGE_1` | [GetNewestItemsHrsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/items/stage1/GetNewestItemsHrsState.java) |
| `AUTOBUY_GET_NEWEST_ITEMS_STAGE_P` | [GetNewestItemsProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/items/stagep/GetNewestItemsProceedState.java) |
| `AUTOSELL` | [UserAutoSellState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/UserAutoSellState.java) |
| `AUTOSELL_UPDATE_FIELD_STAGE_1` | [AutoSellUpdateFieldState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/update/stage1/AutoSellUpdateFieldState.java) |
| `AUTOSELL_UPDATE_FIELD_STAGE_2` | [AutoSellUpdateValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/update/stage2/AutoSellUpdateValueState.java) |
| `AUTOSELL_UPDATE_FIELD_STAGE_P` | [AutoSellUpdateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/update/stagep/AutoSellUpdateProceedState.java) |
| `AUTOSELL_SWITCH_EVAL_MODE` | [SwitchEvalModeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/asswitch/evalmode/SwitchEvalModeState.java) |
| `AUTOSELL_SWITCH_EVAL_MODE_S1` | [SwitchEvalS1.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/asswitch/evals1/SwitchEvalS1.java) |
| `SCORING` | [UserScoringState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/UserScoringState.java) |
| `SCORING_ADD_STAGE_1` | [ScoringAddTypeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/add/stage1/ScoringAddTypeState.java) |
| `SCORING_ADD_STAGE_2` | [ScoringAddProfitState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/add/stage2/ScoringAddProfitState.java) |
| `SCORING_ADD_STAGE_P` | [StageAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/add/stagep/StageAddProceedState.java) |
| `SCORING_EDIT_STAGE_1` | [ScoringEditIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stage1/ScoringEditIdState.java) |
| `SCORING_EDIT_STAGE_2` | [ScoringEditFieldState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stage2/ScoringEditFieldState.java) |
| `SCORING_EDIT_STAGE_3` | [ScoringEditValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stage3/ScoringEditValueState.java) |
| `SCORING_EDIT_STAGE_P` | [ScoringEditProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stagep/ScoringEditProceedState.java) |
| `SCORING_REMOVE_STAGE_1` | [ScoringRemoveIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/remove/stage1/ScoringRemoveIdState.java) |
| `SCORING_REMOVE_STAGE_P` | [ScoringRemoveProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/remove/stagep/ScoringRemoveProceedState.java) |
| `WORDS` | [UserWordsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/UserWordsState.java) |
| `WORDS_ADD_STAGE_CHOOSE` | [WordsAddChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/add/choose/WordsAddChooseState.java) |
| `WORDS_ADD_STAGE_1` | [WordsAddKeyWordState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/add/stage1/WordsAddKeyWordState.java) |
| `WORDS_ADD_STAGE_P` | [WordsAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/add/stagep/WordsAddProceedState.java) |
| `WORDS_GET_STAGE_CHOOSE` | [WordsGetChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/get/choose/WordsGetChooseState.java) |
| `WORDS_GET_STAGE_P` | [WordsGetProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/get/stagep/WordsGetProceedState.java) |
| `WORDS_REMOVE_STAGE_CHOOSE` | [WordsDeleteChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/delete/choose/WordsDeleteChooseState.java) |
| `WORDS_REMOVE_STAGE_1` | [WordsDeleteIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/delete/stage1/WordsDeleteIdState.java) |
| `WORDS_REMOVE_STAGE_P` | [WordsDeleteProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/delete/stagep/WordsDeleteProceedState.java) |
| `WORDS_REMOVE_ALL_STAGE_CHOOSE` | [WordsDeleteAllChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/deleteall/choose/WordsDeleteAllChooseState.java) |
| `WORDS_REMOVE_ALL_STAGE_P` | [WordsDeleteAllProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/deleteall/stagep/WordsDeleteAllProceedState.java) |
| `FOLLOW` | [UserFollowState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/UserFollowState.java) |
| `FOLLOW_CHECK` | [UserFollowCheckState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stage1/UserFollowCheckState.java) |
| `FOLLOW_CHECK_ACCEPT` | [UserFollowAcceptState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stagep/UserFollowAcceptState.java) |
| `FOLLOW_CHECK_DENY` | [UserFollowDenyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stagep/UserFollowDenyState.java) |
| `FOLLOW_STAGE_CHOOSE` | [FollowChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/choose/FollowChooseState.java) |
| `FOLLOW_STAGE_1` | [FollowIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/stage1/FollowIdState.java) |
| `FOLLOW_STAGE_2` | [FollowPcoState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/stage2/FollowPcoState.java) |
| `FOLLOW_STAGE_P` | [FollowProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/stagep/FollowProceedState.java) |
| `FOLLOW_UNFOLLOW_STAGE_1` | [UnfollowIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/unfollow/stage1/UnfollowIdState.java) |
| `FOLLOW_UNFOLLOW_STAGE_P` | [UnfollowProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/unfollow/stagep/UnfollowProceedState.java) |
| `PREFERENCES` | [UserPreferencesState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/preferences/UserPreferencesState.java) |
| `REF` | [UserRefState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/UserRefState.java) |
| `REF_CREATE` | [RefCreateState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/create/RefCreateState.java) |
| `REF_CONNECT_STAGE_1` | [RefConnectCodeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/connect/stage1/RefConnectCodeState.java) |
| `REF_CONNECT_STAGE_P` | [RefConnectProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/connect/stagep/RefConnectProceedState.java) |
| `NOTIFICATION` | Not found in this repository |
| `NOTIFICATION_BALANCE` | [YTBalanceNotifyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/balance/YTBalanceNotifyState.java) |

## Menu labels and state references

### YTBalanceNotifyMenu

[YTBalanceNotifyMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/balance/YTBalanceNotifyMenu.java)

Labels: 💳 Пополнить; ↩️ Назад


### YTBalanceNotifyState

[YTBalanceNotifyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/balance/YTBalanceNotifyState.java)

State references: NOTIFICATION_BALANCE, TOP_UP_STAGE_1, TOP_UP_STAGE_P


### YTBargainAcceptedNotifier

[YTBargainAcceptedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/buy/bargain/YTBargainAcceptedNotifier.java)


### YTBargainCreatedNotifier

[YTBargainCreatedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/buy/bargain/YTBargainCreatedNotifier.java)


### YTBargainFailedNotifier

[YTBargainFailedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/buy/bargain/YTBargainFailedNotifier.java)


### YTBuyCompletedNotifier

[YTBuyCompletedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/buy/YTBuyCompletedNotifier.java)


### YTBuyFailedNotifier

[YTBuyFailedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/buy/YTBuyFailedNotifier.java)


### YTWaitNotifier

[YTWaitNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/general/YTWaitNotifier.java)


### YTMaFileDeletedNotifier

[YTMaFileDeletedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/mafile/YTMaFileDeletedNotifier.java)


### YTPaymentNotifier

[YTPaymentNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/payment/YTPaymentNotifier.java)


### YTInvBaseRestrictNotifier

[YTInvBaseRestrictNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/portfolio/YTInvBaseRestrictNotifier.java)


### YTInvChangedNotifier

[YTInvChangedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/portfolio/YTInvChangedNotifier.java)


### YTInvDeletedNotifier

[YTInvDeletedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/portfolio/YTInvDeletedNotifier.java)


### YTInvUploadNotifier

[YTInvUploadNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/portfolio/YTInvUploadNotifier.java)


### YTSellAddedNotifier

[YTSellAddedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/sell/YTSellAddedNotifier.java)


### YTSellCompletedNotifier

[YTSellCompletedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/sell/YTSellCompletedNotifier.java)


### YTSellFailedNotifier

[YTSellFailedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/sell/YTSellFailedNotifier.java)


### UserGetPriceState

[UserGetPriceState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/getprice/UserGetPriceState.java)

Labels: Покупка; Продажа

State references: GET_PRICE, START


### RefConnectCodeState

[RefConnectCodeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/connect/stage1/RefConnectCodeState.java)

State references: REF_CONNECT_STAGE_1, REF_CONNECT_STAGE_P, SCORING


### RefConnectProceedState

[RefConnectProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/connect/stagep/RefConnectProceedState.java)

State references: REF, REF_CONNECT_STAGE_P


### RefCreateState

[RefCreateState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/create/RefCreateState.java)

State references: REF, REF_CREATE


### UserRefMenu

[UserRefMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/UserRefMenu.java)

Labels: 📝 Создать код; 🔗 Подключить; ↩️ Назад


### UserRefState

[UserRefState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/UserRefState.java)

State references: REF, REF_CONNECT_STAGE_1, REF_CREATE, START


### UserPayAmountState

[UserPayAmountState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/topup/stage1/UserPayAmountState.java)

State references: START, TOP_UP_STAGE_1, TOP_UP_STAGE_P


### UserPayProceedMenu

[UserPayProceedMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/topup/stagep/UserPayProceedMenu.java)

Labels: 💳 Оплатить; ↩️ Назад


### UserPayProceedState

[UserPayProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/topup/stagep/UserPayProceedState.java)

State references: START, TOP_UP_STAGE_P


### AccountsAddChooseState

[AccountsAddChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/choose/AccountsAddChooseState.java)

State references: ACCOUNTS, ACCOUNTS_ADD_STAGE_1_BUYER, ACCOUNTS_ADD_STAGE_1_SELLER, ACCOUNTS_ADD_STAGE_1_WORKER, ACCOUNTS_ADD_STAGE_CHOOSE


### AbstractAddApiState

[AbstractAddApiState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/parent/api/AbstractAddApiState.java)

State references: ACCOUNTS, USER


### TokenAddValueMenu

[TokenAddValueMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/parent/api/TokenAddValueMenu.java)

Labels: ❓ Где я могу найти API-ключ; ↩️ Назад


### AbstractAddProceedState

[AbstractAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/parent/proceed/AbstractAddProceedState.java)

State references: ACCOUNTS


### BuyerAddApiState

[BuyerAddApiState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/buyer/stage1/BuyerAddApiState.java)

State references: ACCOUNTS_ADD_STAGE_1_BUYER, ACCOUNTS_ADD_STAGE_2_BUYER


### AccountsAddTradeUrlMenu

[AccountsAddTradeUrlMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/buyer/stage2/AccountsAddTradeUrlMenu.java)

Labels: ❓ Где я могу узнать Trade-ссылку; ↩️ Назад


### AccountsAddTradeUrlState

[AccountsAddTradeUrlState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/buyer/stage2/AccountsAddTradeUrlState.java)

State references: ACCOUNTS, ACCOUNTS_ADD_STAGE_2_BUYER, ACCOUNTS_ADD_STAGE_P_BUYER


### BuyerAddProceedState

[BuyerAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/buyer/stagep/BuyerAddProceedState.java)

State references: ACCOUNTS_ADD_STAGE_P_BUYER


### SellerAddApiState

[SellerAddApiState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/seller/stage1/SellerAddApiState.java)

State references: ACCOUNTS_ADD_STAGE_1_SELLER, ACCOUNTS_ADD_STAGE_P_SELLER


### SellerAddProceedState

[SellerAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/seller/stagep/SellerAddProceedState.java)

State references: ACCOUNTS_ADD_STAGE_P_SELLER


### AbstractWorkerAddState

[AbstractWorkerAddState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/parent/AbstractWorkerAddState.java)

State references: ACCOUNTS, USER


### WorkerAddLoginState

[WorkerAddLoginState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stage1/WorkerAddLoginState.java)

State references: ACCOUNTS_ADD_STAGE_1_WORKER, ACCOUNTS_ADD_STAGE_2_WORKER


### WorkerAddPasswordState

[WorkerAddPasswordState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stage2/WorkerAddPasswordState.java)

State references: ACCOUNTS_ADD_STAGE_2_WORKER, ACCOUNTS_ADD_STAGE_3_WORKER


### WorkerAddMaFileMenu

[WorkerAddMaFileMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stage3/WorkerAddMaFileMenu.java)

Labels: ❓ Где я могу найти maFile; ↩️ Назад


### WorkerAddMaFileState

[WorkerAddMaFileState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stage3/WorkerAddMaFileState.java)

State references: ACCOUNTS, ACCOUNTS_ADD_STAGE_3_WORKER, ACCOUNTS_ADD_STAGE_P_WORKER


### WorkerAddProceedState

[WorkerAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stagep/WorkerAddProceedState.java)

State references: ACCOUNTS_ADD_STAGE_P_WORKER


### TokenDeleteChooseState

[TokenDeleteChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/delete/choose/TokenDeleteChooseState.java)

State references: ACCOUNTS, ACCOUNTS_REMOVE_STAGE_1, ACCOUNTS_REMOVE_STAGE_CHOOSE


### TokenDeleteIdState

[TokenDeleteIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/delete/stage1/TokenDeleteIdState.java)

State references: ACCOUNTS, ACCOUNTS_REMOVE_STAGE_1, ACCOUNTS_REMOVE_STAGE_P


### TokenDeleteProceedState

[TokenDeleteProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/delete/stagep/TokenDeleteProceedState.java)

State references: ACCOUNTS, ACCOUNTS_REMOVE_STAGE_P


### TokenRenameIdState

[TokenRenameIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/rename/stage1/TokenRenameIdState.java)

State references: ACCOUNTS, ACCOUNTS_RENAME_STAGE_1, ACCOUNTS_RENAME_STAGE_2


### TokenRenameValueState

[TokenRenameValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/rename/stage2/TokenRenameValueState.java)

State references: ACCOUNTS, ACCOUNTS_RENAME_STAGE_2, ACCOUNTS_RENAME_STAGE_P


### TokenRenameProceedState

[TokenRenameProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/rename/stagep/TokenRenameProceedState.java)

State references: ACCOUNTS, ACCOUNTS_RENAME_STAGE_P


### TokenTransferChooseState

[TokenTransferChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/transfer/stage1/TokenTransferChooseState.java)

State references: ACCOUNTS, ACCOUNTS_TRANSFER_STAGE_1, ACCOUNTS_TRANSFER_STAGE_2


### TokenTransferParamsState

[TokenTransferParamsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/transfer/stage2/TokenTransferParamsState.java)

State references: ACCOUNTS, ACCOUNTS_TRANSFER_STAGE_2, ACCOUNTS_TRANSFER_STAGE_P


### TokenTransferProceedState

[TokenTransferProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/transfer/stagep/TokenTransferProceedState.java)

State references: ACCOUNTS, ACCOUNTS_TRANSFER_STAGE_P


### UserAccountsMenu

[UserAccountsMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/UserAccountsMenu.java)

Labels: ◀️; Режим; ▶️; ➕ Добавить; ✈️ Перенести; ✏️ Сменить имя; 🗑️ Удалить; ↩️ Назад


### UserAccountsState

[UserAccountsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/UserAccountsState.java)

State references: ACCOUNTS, ACCOUNTS_ADD_STAGE_CHOOSE, ACCOUNTS_REMOVE_STAGE_CHOOSE, ACCOUNTS_RENAME_STAGE_1, ACCOUNTS_TRANSFER_STAGE_1, USER


### YTPAccountPageTextMenuState

[YTPAccountPageTextMenuState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/util/YTPAccountPageTextMenuState.java)

State references: ACCOUNTS


### SwitchDuplicateState

[SwitchDuplicateState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/abswitch/duplicate/SwitchDuplicateState.java)

State references: AUTOBUY, AUTOBUY_SWITCH_DUPLICATE_MODE


### SwitchFunctionState

[SwitchFunctionState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/abswitch/function/SwitchFunctionState.java)

State references: AUTOBUY, AUTOBUY_SWITCH_FUNCTION_TYPE


### GetNewestItemsHrsState

[GetNewestItemsHrsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/items/stage1/GetNewestItemsHrsState.java)

State references: AUTOBUY, AUTOBUY_GET_NEWEST_ITEMS_STAGE_1, AUTOBUY_GET_NEWEST_ITEMS_STAGE_P


### GetNewestItemsProceedState

[GetNewestItemsProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/items/stagep/GetNewestItemsProceedState.java)

State references: AUTOBUY, AUTOBUY_GET_NEWEST_ITEMS_STAGE_P


### ScoringAddTypeState

[ScoringAddTypeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/add/stage1/ScoringAddTypeState.java)

State references: SCORING, SCORING_ADD_STAGE_1, SCORING_ADD_STAGE_2


### ScoringAddProfitState

[ScoringAddProfitState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/add/stage2/ScoringAddProfitState.java)

State references: SCORING, SCORING_ADD_STAGE_2, SCORING_ADD_STAGE_P


### StageAddProceedState

[StageAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/add/stagep/StageAddProceedState.java)

State references: SCORING, SCORING_ADD_STAGE_P


### ScoringEditIdState

[ScoringEditIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stage1/ScoringEditIdState.java)

State references: SCORING, SCORING_EDIT_STAGE_1, SCORING_EDIT_STAGE_2


### ScoringEditFieldState

[ScoringEditFieldState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stage2/ScoringEditFieldState.java)

State references: SCORING, SCORING_EDIT_STAGE_2, SCORING_EDIT_STAGE_3


### ScoringEditValueState

[ScoringEditValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stage3/ScoringEditValueState.java)

State references: SCORING, SCORING_EDIT_STAGE_3, SCORING_EDIT_STAGE_P


### ScoringEditProceedState

[ScoringEditProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stagep/ScoringEditProceedState.java)

State references: SCORING, SCORING_EDIT_STAGE_P


### ItemScoringTypeMenu

[ItemScoringTypeMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/ItemScoringTypeMenu.java)

Labels: ↩️ Назад


### ScoringRemoveIdState

[ScoringRemoveIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/remove/stage1/ScoringRemoveIdState.java)

State references: SCORING, SCORING_REMOVE_STAGE_1, SCORING_REMOVE_STAGE_P


### ScoringRemoveProceedState

[ScoringRemoveProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/remove/stagep/ScoringRemoveProceedState.java)

State references: SCORING, SCORING_REMOVE_STAGE_P


### UserScoringMenu

[UserScoringMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/UserScoringMenu.java)

Labels: ➕ Добавить; ✏️ Изменить; 🗑️ Удалить; ↩️ Назад


### UserScoringState

[UserScoringState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/UserScoringState.java)

State references: AUTOBUY, SCORING, SCORING_ADD_STAGE_1, SCORING_EDIT_STAGE_1, SCORING_REMOVE_STAGE_1


### AutoBuyUpdateFieldState

[AutoBuyUpdateFieldState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/update/stage1/AutoBuyUpdateFieldState.java)

State references: AUTOBUY, AUTOBUY_UPDATE_FIELD_STAGE_1, AUTOBUY_UPDATE_FIELD_STAGE_2


### AutoBuyUpdateValueState

[AutoBuyUpdateValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/update/stage2/AutoBuyUpdateValueState.java)

State references: AUTOBUY, AUTOBUY_UPDATE_FIELD_STAGE_2, AUTOBUY_UPDATE_FIELD_STAGE_P


### AutoBuyUpdateProceedState

[AutoBuyUpdateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/update/stagep/AutoBuyUpdateProceedState.java)

State references: AUTOBUY, AUTOBUY_UPDATE_FIELD_STAGE_P


### UserAutoBuyMenu

[UserAutoBuyMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/UserAutoBuyMenu.java)

Labels: ⚙️ Изменить параметры; 🔄 Функцию; 🔄 Дублирование; 🔢 Scoring; 📚 Words; 🌐 Общая история лотов; ↩️ Назад


### UserAutoBuyState

[UserAutoBuyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/UserAutoBuyState.java)

State references: AUTOBUY, AUTOBUY_GET_NEWEST_ITEMS_STAGE_1, AUTOBUY_SWITCH_DUPLICATE_MODE, AUTOBUY_SWITCH_FUNCTION_TYPE, AUTOBUY_UPDATE_FIELD_STAGE_1, PARAMS, SCORING, WORDS


### AbstractWordsChooseState

[AbstractWordsChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/AbstractWordsChooseState.java)


### WordsAddChooseState

[WordsAddChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/add/choose/WordsAddChooseState.java)

State references: WORDS, WORDS_ADD_STAGE_1, WORDS_ADD_STAGE_CHOOSE


### WordsAddKeyWordState

[WordsAddKeyWordState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/add/stage1/WordsAddKeyWordState.java)

State references: WORDS, WORDS_ADD_STAGE_1, WORDS_ADD_STAGE_P


### WordsAddProceedState

[WordsAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/add/stagep/WordsAddProceedState.java)

State references: WORDS, WORDS_ADD_STAGE_P


### WordsDeleteChooseState

[WordsDeleteChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/delete/choose/WordsDeleteChooseState.java)

State references: WORDS, WORDS_REMOVE_STAGE_1, WORDS_REMOVE_STAGE_CHOOSE


### WordsDeleteIdState

[WordsDeleteIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/delete/stage1/WordsDeleteIdState.java)

State references: WORDS, WORDS_REMOVE_STAGE_1, WORDS_REMOVE_STAGE_P


### WordsDeleteProceedState

[WordsDeleteProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/delete/stagep/WordsDeleteProceedState.java)

State references: WORDS, WORDS_REMOVE_STAGE_P


### WordsDeleteAllChooseState

[WordsDeleteAllChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/deleteall/choose/WordsDeleteAllChooseState.java)

State references: WORDS, WORDS_REMOVE_ALL_STAGE_CHOOSE, WORDS_REMOVE_ALL_STAGE_P


### WordsDeleteAllProceedState

[WordsDeleteAllProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/deleteall/stagep/WordsDeleteAllProceedState.java)

State references: WORDS, WORDS_REMOVE_ALL_STAGE_P


### WordsGetChooseState

[WordsGetChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/get/choose/WordsGetChooseState.java)

State references: WORDS, WORDS_GET_STAGE_CHOOSE, WORDS_GET_STAGE_P


### WordsGetProceedState

[WordsGetProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/get/stagep/WordsGetProceedState.java)

State references: WORDS, WORDS_GET_STAGE_P


### UserWordsMenu

[UserWordsMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/UserWordsMenu.java)

Labels: 📋 Посмотреть; ➕ Добавить; 🗑️ Удалить; 💥 Удалить ВСЕ; ↩️ Назад


### UserWordsState

[UserWordsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/UserWordsState.java)

State references: AUTOBUY, WORDS, WORDS_ADD_STAGE_CHOOSE, WORDS_GET_STAGE_CHOOSE, WORDS_REMOVE_ALL_STAGE_CHOOSE, WORDS_REMOVE_STAGE_CHOOSE


### SwitchEvalModeState

[SwitchEvalModeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/asswitch/evalmode/SwitchEvalModeState.java)

State references: AUTOSELL, AUTOSELL_SWITCH_EVAL_MODE


### AutoSellUpdateFieldState

[AutoSellUpdateFieldState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/update/stage1/AutoSellUpdateFieldState.java)

State references: AUTOSELL, AUTOSELL_UPDATE_FIELD_STAGE_1, AUTOSELL_UPDATE_FIELD_STAGE_2, SCORING


### AutoSellUpdateValueState

[AutoSellUpdateValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/update/stage2/AutoSellUpdateValueState.java)

State references: AUTOSELL, AUTOSELL_UPDATE_FIELD_STAGE_2, AUTOSELL_UPDATE_FIELD_STAGE_P


### AutoSellUpdateProceedState

[AutoSellUpdateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/update/stagep/AutoSellUpdateProceedState.java)

State references: AUTOSELL, AUTOSELL_UPDATE_FIELD_STAGE_P


### UserAutoSellMenu

[UserAutoSellMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/UserAutoSellMenu.java)

Labels: ⚙️ Изменить параметры; 🔄 EvalMode; 🔄 EvalModeS1; ↩️ Назад


### UserAutoSellState

[UserAutoSellState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/UserAutoSellState.java)

State references: AUTOSELL, AUTOSELL_SWITCH_EVAL_MODE, AUTOSELL_SWITCH_EVAL_MODE_S1, AUTOSELL_UPDATE_FIELD_STAGE_1, PARAMS


### AbstractCreateState

[AbstractCreateState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/prototype/AbstractCreateState.java)


### CreateSourceState

[CreateSourceState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/stage1/CreateSourceState.java)

State references: PARAMS, PARAMS_CREATE_STAGE_1, PARAMS_CREATE_STAGE_2


### CreateDestinationState

[CreateDestinationState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/stage2/CreateDestinationState.java)

State references: PARAMS_CREATE_STAGE_2, PARAMS_CREATE_STAGE_P


### CreateProceedState

[CreateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/stagep/CreateProceedState.java)

State references: PARAMS, PARAMS_CREATE_STAGE_P


### DeleteRequestMenu

[DeleteRequestMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/delete/stage1/DeleteRequestMenu.java)

Labels: ✅ Подтвердить удаление; ❌ Отменить удаление


### DeleteRequestState

[DeleteRequestState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/delete/stage1/DeleteRequestState.java)

State references: PARAMS_DELETE_STAGE_1, PARAMS_DELETE_STAGE_P


### DeleteProceedState

[DeleteProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/delete/stagep/DeleteProceedState.java)

State references: PARAMS, PARAMS_DELETE_STAGE_P


### UserFollowCheckMenu

[UserFollowCheckMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stage1/UserFollowCheckMenu.java)

Labels: ✅ Принять; ❌ Отклонить; ↩️ Назад


### UserFollowCheckState

[UserFollowCheckState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stage1/UserFollowCheckState.java)

State references: FOLLOW, FOLLOW_CHECK, FOLLOW_CHECK_ACCEPT, FOLLOW_CHECK_DENY


### AbstractUserFollowProceedState

[AbstractUserFollowProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stagep/AbstractUserFollowProceedState.java)

State references: FOLLOW_CHECK


### UserFollowAcceptState

[UserFollowAcceptState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stagep/UserFollowAcceptState.java)

State references: FOLLOW_CHECK_ACCEPT


### UserFollowDenyState

[UserFollowDenyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stagep/UserFollowDenyState.java)

State references: FOLLOW_CHECK_DENY


### FollowChooseState

[FollowChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/choose/FollowChooseState.java)

State references: FOLLOW_STAGE_1, FOLLOW_STAGE_CHOOSE


### FollowIdState

[FollowIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/stage1/FollowIdState.java)

State references: FOLLOW, FOLLOW_STAGE_1, FOLLOW_STAGE_2


### FollowPcoState

[FollowPcoState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/stage2/FollowPcoState.java)

State references: FOLLOW, FOLLOW_STAGE_2, FOLLOW_STAGE_P


### FollowProceedState

[FollowProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/stagep/FollowProceedState.java)

State references: FOLLOW, FOLLOW_STAGE_P


### UnfollowIdState

[UnfollowIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/unfollow/stage1/UnfollowIdState.java)

State references: FOLLOW_UNFOLLOW_STAGE_1, FOLLOW_UNFOLLOW_STAGE_P, SCORING, WORDS


### UnfollowProceedState

[UnfollowProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/unfollow/stagep/UnfollowProceedState.java)

State references: FOLLOW, FOLLOW_UNFOLLOW_STAGE_P


### UserFollowMenu

[UserFollowMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/UserFollowMenu.java)

Labels: 👥 Заявки; ➕ Создать заявку; 🗑️ Удалить; ↩️ Назад


### UserFollowState

[UserFollowState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/UserFollowState.java)

State references: FOLLOW, FOLLOW_CHECK, FOLLOW_STAGE_CHOOSE, FOLLOW_UNFOLLOW_STAGE_1, PARAMS


### ParamsListState

[ParamsListState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/list/ParamsListState.java)

State references: PARAMS, PARAMS_LIST


### ParamsRenameIdState

[ParamsRenameIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/rename/stage1/ParamsRenameIdState.java)

State references: PARAMS, PARAMS_RENAME_STAGE_1, PARAMS_RENAME_STAGE_2


### ParamsRenameValueState

[ParamsRenameValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/rename/stage2/ParamsRenameValueState.java)

State references: PARAMS_RENAME_STAGE_2, PARAMS_RENAME_STAGE_P, USER


### ParamsRenameProceedState

[ParamsRenameProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/rename/stagep/ParamsRenameProceedState.java)

State references: PARAMS, PARAMS_RENAME_STAGE_P


### UserDeepParamsMenu

[UserDeepParamsMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/UserDeepParamsMenu.java)

Labels: 🧊 Режим: Рыночный; 🔥 Режим: Баргейны; 📥 Автопокупка; 📤 Автопродажа; ✏️ Переименовать; 👥 Следование; ➕ Новые; 📋 Список всех; 🗑️ Удалить; ⚙️ Настройки удобства; ↩️ Назад; ◀️ Быстрая


### UserDeepParamsState

[UserDeepParamsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/UserDeepParamsState.java)

State references: AUTOBUY, AUTOSELL, FOLLOW, PARAMS, PARAMS_CREATE_STAGE_1, PARAMS_DELETE_STAGE_1, PARAMS_LIST, PARAMS_RENAME_STAGE_1, PREFERENCES, START, USER


### ClassicTableMenu

[ClassicTableMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/ClassicTableMenu.java)

Labels: 🌐 Онлайн-редактор; ↩️ Назад


### UserTableMenu

[UserTableMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/UserTableMenu.java)

Labels: 🎒 Инвентарь; 💵 Витрина; 🕒 Ожидание; 🔁 Восстановить; 🗂️ История; ↩️ Назад


### UserTableState

[UserTableState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/UserTableState.java)

State references: PORTFOLIO, PORTFOLIO_HISTORY_STAGE_CHOOSE, PORTFOLIO_V2_INVENTORY_STAGE_1, PORTFOLIO_V2_RESTORE_STAGE_1, PORTFOLIO_V2_SELLING_STAGE_1, PORTFOLIO_WAITING, USER


### TableHistoryModeState

[TableHistoryModeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/choose/TableHistoryModeState.java)

State references: PORTFOLIO, PORTFOLIO_HISTORY_STAGE_1, PORTFOLIO_HISTORY_STAGE_CHOOSE


### TableHistoryPeriodState

[TableHistoryPeriodState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stage1/TableHistoryPeriodState.java)

State references: PORTFOLIO, PORTFOLIO_HISTORY_STAGE_1, PORTFOLIO_HISTORY_STAGE_P_BUY, PORTFOLIO_HISTORY_STAGE_P_SELL


### AbstractHistoryProceedState

[AbstractHistoryProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stagep/parent/AbstractHistoryProceedState.java)

State references: PORTFOLIO


### TableBuyHistoryProceedState

[TableBuyHistoryProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stagep/TableBuyHistoryProceedState.java)

State references: PORTFOLIO_HISTORY_STAGE_P_BUY


### TableSellHistoryProceedState

[TableSellHistoryProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stagep/TableSellHistoryProceedState.java)

State references: PORTFOLIO_HISTORY_STAGE_P_SELL


### TableWaitingState

[TableWaitingState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/waiting/TableWaitingState.java)

State references: PORTFOLIO, PORTFOLIO_WAITING


### TableV2InventoryListState

[TableV2InventoryListState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/inventory/stage1/TableV2InventoryListState.java)

State references: PORTFOLIO, PORTFOLIO_V2_INVENTORY_STAGE_1, PORTFOLIO_V2_INVENTORY_STAGE_P


### TableV2InventoryProceedState

[TableV2InventoryProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/inventory/stagep/TableV2InventoryProceedState.java)

State references: PORTFOLIO, PORTFOLIO_V2_INVENTORY_STAGE_P


### TableV2RestoreAgreementMenu

[TableV2RestoreAgreementMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/restore/stage1/TableV2RestoreAgreementMenu.java)

Labels: ✅ Я согласен с условиями восстановления; ↩️ Назад


### TableV2RestoreAgreementState

[TableV2RestoreAgreementState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/restore/stage1/TableV2RestoreAgreementState.java)

State references: PORTFOLIO, PORTFOLIO_V2_RESTORE_STAGE_1, PORTFOLIO_V2_RESTORE_STAGE_P


### TableV2RestoreProceedState

[TableV2RestoreProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/restore/stagep/TableV2RestoreProceedState.java)

State references: PORTFOLIO, PORTFOLIO_V2_RESTORE_STAGE_P


### TableV2SellingListState

[TableV2SellingListState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/selling/stage1/TableV2SellingListState.java)

State references: PORTFOLIO, PORTFOLIO_V2_SELLING_STAGE_1, PORTFOLIO_V2_SELLING_STAGE_P


### TableV2SellingProceedState

[TableV2SellingProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/selling/stagep/TableV2SellingProceedState.java)

State references: PORTFOLIO, PORTFOLIO_V2_SELLING_STAGE_P


### YTPTableState

[YTPTableState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/YTPTableState.java)

State references: PORTFOLIO


### UserPreferencesMenu

[UserPreferencesMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/preferences/UserPreferencesMenu.java)

Labels: 🟢 Сообщения торгов; 🔴 Сообщения торгов; ↩️ Назад


### UserPreferencesState

[UserPreferencesState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/preferences/UserPreferencesState.java)

State references: PARAMS, PREFERENCES


### ParamsSwitchIdState

[ParamsSwitchIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/pswitch/stage1/ParamsSwitchIdState.java)

State references: PARAMS_SWITCH_STAGE_1, PARAMS_SWITCH_STAGE_P, USER


### ParamsSwitchProceedState

[ParamsSwitchProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/pswitch/stagep/ParamsSwitchProceedState.java)

State references: PARAMS_SWITCH_STAGE_P, USER


### QuickConfigDisableState

[QuickConfigDisableState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/disable/QuickConfigDisableState.java)

State references: USER, USER_QUICK_CONFIG_DISABLE


### AbstractQuickConfigGradeState

[AbstractQuickConfigGradeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/prototype/AbstractQuickConfigGradeState.java)


### QuickConfigGradeMenu

[QuickConfigGradeMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/prototype/QuickConfigGradeMenu.java)

Labels: 🔓 Мягкий; ⚖️ Умеренный; 🔒 Строгий; ☠️ Тотальный; 🚫 Выключить; ↩️ Назад


### ConfigCreateAutoBuyState

[ConfigCreateAutoBuyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stage1/ConfigCreateAutoBuyState.java)

State references: USER, USER_QUICK_CONFIG_INIT_STAGE_1, USER_QUICK_CONFIG_INIT_STAGE_2


### ConfigCreateAutoSellState

[ConfigCreateAutoSellState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stage2/ConfigCreateAutoSellState.java)

State references: USER, USER_QUICK_CONFIG_INIT_STAGE_2, USER_QUICK_CONFIG_INIT_STAGE_3


### ConfigCreateBankSizeState

[ConfigCreateBankSizeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stage3/ConfigCreateBankSizeState.java)

State references: START, USER_QUICK_CONFIG_INIT_STAGE_3, USER_QUICK_CONFIG_INIT_STAGE_P


### ConfigCreateProceedState

[ConfigCreateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stagep/ConfigCreateProceedState.java)

State references: USER, USER_QUICK_CONFIG_INIT_STAGE_P


### UserParamsMenu

[UserParamsMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/UserParamsMenu.java)

Labels: 🔋 Быстрая настройка; 🪫 Быстрая настройка; 🔑 Аккаунты; 💼 Портфель; 🔄 Сменить; 🟢 Покупка; 🔴 Покупка; 🟢 Продажа; 🔴 Продажа; ↩️ Назад; ▶️ Углублённая


### UserParamsState

[UserParamsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/UserParamsState.java)

State references: ACCOUNTS, PARAMS, PARAMS_SWITCH_STAGE_1, PORTFOLIO, START, USER, USER_QUICK_CONFIG_DISABLE, USER_QUICK_CONFIG_INIT_STAGE_1


### UserStartMenu

[UserStartMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/UserStartMenu.java)

Labels: 📊 Начать торговлю; 💎 Реферальная программа; 💳 Пополнить; 💰 Узнать цены; 📢 Группа; 🆘 Поддержка


### UserStartState

[UserStartState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/UserStartState.java)

State references: GET_PRICE, REF, START, TOP_UP_STAGE_1, USER


### UserMenu

[UserMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/UserMenu.java)

## Source inventory

- [InnerKeyEntity.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/keygen/entities/InnerKeyEntity.java) - `keygen/entities/InnerKeyEntity.java`
- [InnerKeyRepository.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/keygen/repositories/InnerKeyRepository.java) - `keygen/repositories/InnerKeyRepository.java`
- [InnerKeyManagerService.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/keygen/service/InnerKeyManagerService.java) - `keygen/service/InnerKeyManagerService.java`
- [Main.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/Main.java) - `Main.java`
- [YTBalanceNotifyMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/balance/YTBalanceNotifyMenu.java) - `telegram/menu/notification/balance/YTBalanceNotifyMenu.java`
- [YTBalanceNotifyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/balance/YTBalanceNotifyState.java) - `telegram/menu/notification/balance/YTBalanceNotifyState.java`
- [YTBargainAcceptedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/buy/bargain/YTBargainAcceptedNotifier.java) - `telegram/menu/notification/buy/bargain/YTBargainAcceptedNotifier.java`
- [YTBargainCreatedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/buy/bargain/YTBargainCreatedNotifier.java) - `telegram/menu/notification/buy/bargain/YTBargainCreatedNotifier.java`
- [YTBargainFailedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/buy/bargain/YTBargainFailedNotifier.java) - `telegram/menu/notification/buy/bargain/YTBargainFailedNotifier.java`
- [YTBuyCompletedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/buy/YTBuyCompletedNotifier.java) - `telegram/menu/notification/buy/YTBuyCompletedNotifier.java`
- [YTBuyFailedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/buy/YTBuyFailedNotifier.java) - `telegram/menu/notification/buy/YTBuyFailedNotifier.java`
- [YTWaitNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/general/YTWaitNotifier.java) - `telegram/menu/notification/general/YTWaitNotifier.java`
- [YTMaFileDeletedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/mafile/YTMaFileDeletedNotifier.java) - `telegram/menu/notification/mafile/YTMaFileDeletedNotifier.java`
- [YTPaymentNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/payment/YTPaymentNotifier.java) - `telegram/menu/notification/payment/YTPaymentNotifier.java`
- [YTInvBaseRestrictNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/portfolio/YTInvBaseRestrictNotifier.java) - `telegram/menu/notification/portfolio/YTInvBaseRestrictNotifier.java`
- [YTInvChangedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/portfolio/YTInvChangedNotifier.java) - `telegram/menu/notification/portfolio/YTInvChangedNotifier.java`
- [YTInvDeletedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/portfolio/YTInvDeletedNotifier.java) - `telegram/menu/notification/portfolio/YTInvDeletedNotifier.java`
- [YTInvUploadNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/portfolio/YTInvUploadNotifier.java) - `telegram/menu/notification/portfolio/YTInvUploadNotifier.java`
- [YTSellAddedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/sell/YTSellAddedNotifier.java) - `telegram/menu/notification/sell/YTSellAddedNotifier.java`
- [YTSellCompletedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/sell/YTSellCompletedNotifier.java) - `telegram/menu/notification/sell/YTSellCompletedNotifier.java`
- [YTSellFailedNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/notification/sell/YTSellFailedNotifier.java) - `telegram/menu/notification/sell/YTSellFailedNotifier.java`
- [UserGetPriceState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/getprice/UserGetPriceState.java) - `telegram/menu/start/getprice/UserGetPriceState.java`
- [RefConnectData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/connect/RefConnectData.java) - `telegram/menu/start/ref/connect/RefConnectData.java`
- [RefConnectRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/connect/RefConnectRegistry.java) - `telegram/menu/start/ref/connect/RefConnectRegistry.java`
- [RefConnectCodeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/connect/stage1/RefConnectCodeState.java) - `telegram/menu/start/ref/connect/stage1/RefConnectCodeState.java`
- [RefConnectProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/connect/stagep/RefConnectProceedState.java) - `telegram/menu/start/ref/connect/stagep/RefConnectProceedState.java`
- [RefCreateState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/create/RefCreateState.java) - `telegram/menu/start/ref/create/RefCreateState.java`
- [UserRefMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/UserRefMenu.java) - `telegram/menu/start/ref/UserRefMenu.java`
- [UserRefState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/ref/UserRefState.java) - `telegram/menu/start/ref/UserRefState.java`
- [UserPayAmountState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/topup/stage1/UserPayAmountState.java) - `telegram/menu/start/topup/stage1/UserPayAmountState.java`
- [UserPayProceedMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/topup/stagep/UserPayProceedMenu.java) - `telegram/menu/start/topup/stagep/UserPayProceedMenu.java`
- [UserPayProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/topup/stagep/UserPayProceedState.java) - `telegram/menu/start/topup/stagep/UserPayProceedState.java`
- [UserPayData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/topup/UserPayData.java) - `telegram/menu/start/topup/UserPayData.java`
- [UserPayRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/topup/UserPayRegistry.java) - `telegram/menu/start/topup/UserPayRegistry.java`
- [AccountsAddChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/choose/AccountsAddChooseState.java) - `telegram/menu/start/user/accounts/add/choose/AccountsAddChooseState.java`
- [AbstractAddApiState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/parent/api/AbstractAddApiState.java) - `telegram/menu/start/user/accounts/add/parent/api/AbstractAddApiState.java`
- [TokenAddValueMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/parent/api/TokenAddValueMenu.java) - `telegram/menu/start/user/accounts/add/parent/api/TokenAddValueMenu.java`
- [AbstractAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/parent/proceed/AbstractAddProceedState.java) - `telegram/menu/start/user/accounts/add/parent/proceed/AbstractAddProceedState.java`
- [UserApiData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/parent/registry/UserApiData.java) - `telegram/menu/start/user/accounts/add/parent/registry/UserApiData.java`
- [UserApiRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/parent/registry/UserApiRegistry.java) - `telegram/menu/start/user/accounts/add/parent/registry/UserApiRegistry.java`
- [BuyerAddApiState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/buyer/stage1/BuyerAddApiState.java) - `telegram/menu/start/user/accounts/add/stages/buyer/stage1/BuyerAddApiState.java`
- [AccountsAddTradeUrlMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/buyer/stage2/AccountsAddTradeUrlMenu.java) - `telegram/menu/start/user/accounts/add/stages/buyer/stage2/AccountsAddTradeUrlMenu.java`
- [AccountsAddTradeUrlState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/buyer/stage2/AccountsAddTradeUrlState.java) - `telegram/menu/start/user/accounts/add/stages/buyer/stage2/AccountsAddTradeUrlState.java`
- [BuyerAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/buyer/stagep/BuyerAddProceedState.java) - `telegram/menu/start/user/accounts/add/stages/buyer/stagep/BuyerAddProceedState.java`
- [SellerAddApiState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/seller/stage1/SellerAddApiState.java) - `telegram/menu/start/user/accounts/add/stages/seller/stage1/SellerAddApiState.java`
- [SellerAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/seller/stagep/SellerAddProceedState.java) - `telegram/menu/start/user/accounts/add/stages/seller/stagep/SellerAddProceedState.java`
- [AbstractWorkerAddState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/parent/AbstractWorkerAddState.java) - `telegram/menu/start/user/accounts/add/stages/worker/parent/AbstractWorkerAddState.java`
- [WorkerAddLoginState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stage1/WorkerAddLoginState.java) - `telegram/menu/start/user/accounts/add/stages/worker/stage1/WorkerAddLoginState.java`
- [WorkerAddPasswordState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stage2/WorkerAddPasswordState.java) - `telegram/menu/start/user/accounts/add/stages/worker/stage2/WorkerAddPasswordState.java`
- [WorkerAddMaFileMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stage3/WorkerAddMaFileMenu.java) - `telegram/menu/start/user/accounts/add/stages/worker/stage3/WorkerAddMaFileMenu.java`
- [WorkerAddMaFileState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stage3/WorkerAddMaFileState.java) - `telegram/menu/start/user/accounts/add/stages/worker/stage3/WorkerAddMaFileState.java`
- [WorkerAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/stagep/WorkerAddProceedState.java) - `telegram/menu/start/user/accounts/add/stages/worker/stagep/WorkerAddProceedState.java`
- [WorkerAddData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/WorkerAddData.java) - `telegram/menu/start/user/accounts/add/stages/worker/WorkerAddData.java`
- [WorkerAddRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/add/stages/worker/WorkerAddRegistry.java) - `telegram/menu/start/user/accounts/add/stages/worker/WorkerAddRegistry.java`
- [TokenDeleteChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/delete/choose/TokenDeleteChooseState.java) - `telegram/menu/start/user/accounts/delete/choose/TokenDeleteChooseState.java`
- [TokenDeleteIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/delete/stage1/TokenDeleteIdState.java) - `telegram/menu/start/user/accounts/delete/stage1/TokenDeleteIdState.java`
- [TokenDeleteProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/delete/stagep/TokenDeleteProceedState.java) - `telegram/menu/start/user/accounts/delete/stagep/TokenDeleteProceedState.java`
- [UserTokenDeleteData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/delete/UserTokenDeleteData.java) - `telegram/menu/start/user/accounts/delete/UserTokenDeleteData.java`
- [UserTokenDeleteRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/delete/UserTokenDeleteRegistry.java) - `telegram/menu/start/user/accounts/delete/UserTokenDeleteRegistry.java`
- [TokenRenameIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/rename/stage1/TokenRenameIdState.java) - `telegram/menu/start/user/accounts/rename/stage1/TokenRenameIdState.java`
- [TokenRenameValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/rename/stage2/TokenRenameValueState.java) - `telegram/menu/start/user/accounts/rename/stage2/TokenRenameValueState.java`
- [TokenRenameProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/rename/stagep/TokenRenameProceedState.java) - `telegram/menu/start/user/accounts/rename/stagep/TokenRenameProceedState.java`
- [UserTokenRenameRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/rename/UserTokenRenameRegistry.java) - `telegram/menu/start/user/accounts/rename/UserTokenRenameRegistry.java`
- [TokenTransferChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/transfer/stage1/TokenTransferChooseState.java) - `telegram/menu/start/user/accounts/transfer/stage1/TokenTransferChooseState.java`
- [TokenTransferParamsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/transfer/stage2/TokenTransferParamsState.java) - `telegram/menu/start/user/accounts/transfer/stage2/TokenTransferParamsState.java`
- [TokenTransferProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/transfer/stagep/TokenTransferProceedState.java) - `telegram/menu/start/user/accounts/transfer/stagep/TokenTransferProceedState.java`
- [UserTokenTransferRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/transfer/UserTokenTransferRegistry.java) - `telegram/menu/start/user/accounts/transfer/UserTokenTransferRegistry.java`
- [UserAccountsMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/UserAccountsMenu.java) - `telegram/menu/start/user/accounts/UserAccountsMenu.java`
- [UserAccountsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/UserAccountsState.java) - `telegram/menu/start/user/accounts/UserAccountsState.java`
- [AccountsChooseOption.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/util/AccountsChooseOption.java) - `telegram/menu/start/user/accounts/util/AccountsChooseOption.java`
- [UserAccountsMetaData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/util/UserAccountsMetaData.java) - `telegram/menu/start/user/accounts/util/UserAccountsMetaData.java`
- [UserAccountsMode.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/util/UserAccountsMode.java) - `telegram/menu/start/user/accounts/util/UserAccountsMode.java`
- [YTPAccountPageTextMenuState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/util/YTPAccountPageTextMenuState.java) - `telegram/menu/start/user/accounts/util/YTPAccountPageTextMenuState.java`
- [YTPAccountsPageProcessorDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/util/YTPAccountsPageProcessorDto.java) - `telegram/menu/start/user/accounts/util/YTPAccountsPageProcessorDto.java`
- [YTPPageProcessor.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/accounts/util/YTPPageProcessor.java) - `telegram/menu/start/user/accounts/util/YTPPageProcessor.java`
- [SwitchDuplicateState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/abswitch/duplicate/SwitchDuplicateState.java) - `telegram/menu/start/user/params/autobuy/abswitch/duplicate/SwitchDuplicateState.java`
- [SwitchFunctionState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/abswitch/function/SwitchFunctionState.java) - `telegram/menu/start/user/params/autobuy/abswitch/function/SwitchFunctionState.java`
- [GetNewestItemsData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/items/GetNewestItemsData.java) - `telegram/menu/start/user/params/autobuy/items/GetNewestItemsData.java`
- [GetNewestItemsRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/items/GetNewestItemsRegistry.java) - `telegram/menu/start/user/params/autobuy/items/GetNewestItemsRegistry.java`
- [GetNewestItemsHrsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/items/stage1/GetNewestItemsHrsState.java) - `telegram/menu/start/user/params/autobuy/items/stage1/GetNewestItemsHrsState.java`
- [GetNewestItemsProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/items/stagep/GetNewestItemsProceedState.java) - `telegram/menu/start/user/params/autobuy/items/stagep/GetNewestItemsProceedState.java`
- [ScoringAddData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/add/ScoringAddData.java) - `telegram/menu/start/user/params/autobuy/scoring/add/ScoringAddData.java`
- [ScoringAddRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/add/ScoringAddRegistry.java) - `telegram/menu/start/user/params/autobuy/scoring/add/ScoringAddRegistry.java`
- [ScoringAddTypeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/add/stage1/ScoringAddTypeState.java) - `telegram/menu/start/user/params/autobuy/scoring/add/stage1/ScoringAddTypeState.java`
- [ScoringAddProfitState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/add/stage2/ScoringAddProfitState.java) - `telegram/menu/start/user/params/autobuy/scoring/add/stage2/ScoringAddProfitState.java`
- [StageAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/add/stagep/StageAddProceedState.java) - `telegram/menu/start/user/params/autobuy/scoring/add/stagep/StageAddProceedState.java`
- [ScoringEditData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/ScoringEditData.java) - `telegram/menu/start/user/params/autobuy/scoring/edit/ScoringEditData.java`
- [ScoringEditRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/ScoringEditRegistry.java) - `telegram/menu/start/user/params/autobuy/scoring/edit/ScoringEditRegistry.java`
- [ScoringEditIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stage1/ScoringEditIdState.java) - `telegram/menu/start/user/params/autobuy/scoring/edit/stage1/ScoringEditIdState.java`
- [ScoringEditFieldState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stage2/ScoringEditFieldState.java) - `telegram/menu/start/user/params/autobuy/scoring/edit/stage2/ScoringEditFieldState.java`
- [ScoringEditValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stage3/ScoringEditValueState.java) - `telegram/menu/start/user/params/autobuy/scoring/edit/stage3/ScoringEditValueState.java`
- [ScoringEditProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/edit/stagep/ScoringEditProceedState.java) - `telegram/menu/start/user/params/autobuy/scoring/edit/stagep/ScoringEditProceedState.java`
- [ItemScoringTypeMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/ItemScoringTypeMenu.java) - `telegram/menu/start/user/params/autobuy/scoring/ItemScoringTypeMenu.java`
- [ScoringRemoveData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/remove/ScoringRemoveData.java) - `telegram/menu/start/user/params/autobuy/scoring/remove/ScoringRemoveData.java`
- [ScoringRemoveRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/remove/ScoringRemoveRegistry.java) - `telegram/menu/start/user/params/autobuy/scoring/remove/ScoringRemoveRegistry.java`
- [ScoringRemoveIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/remove/stage1/ScoringRemoveIdState.java) - `telegram/menu/start/user/params/autobuy/scoring/remove/stage1/ScoringRemoveIdState.java`
- [ScoringRemoveProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/remove/stagep/ScoringRemoveProceedState.java) - `telegram/menu/start/user/params/autobuy/scoring/remove/stagep/ScoringRemoveProceedState.java`
- [UserScoringMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/UserScoringMenu.java) - `telegram/menu/start/user/params/autobuy/scoring/UserScoringMenu.java`
- [UserScoringState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/scoring/UserScoringState.java) - `telegram/menu/start/user/params/autobuy/scoring/UserScoringState.java`
- [AutoBuyUpdateFieldState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/update/stage1/AutoBuyUpdateFieldState.java) - `telegram/menu/start/user/params/autobuy/update/stage1/AutoBuyUpdateFieldState.java`
- [AutoBuyUpdateValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/update/stage2/AutoBuyUpdateValueState.java) - `telegram/menu/start/user/params/autobuy/update/stage2/AutoBuyUpdateValueState.java`
- [AutoBuyUpdateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/update/stagep/AutoBuyUpdateProceedState.java) - `telegram/menu/start/user/params/autobuy/update/stagep/AutoBuyUpdateProceedState.java`
- [UserAutoBuyUpdateData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/update/UserAutoBuyUpdateData.java) - `telegram/menu/start/user/params/autobuy/update/UserAutoBuyUpdateData.java`
- [UserAutoBuyUpdateRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/update/UserAutoBuyUpdateRegistry.java) - `telegram/menu/start/user/params/autobuy/update/UserAutoBuyUpdateRegistry.java`
- [UserAutoBuyMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/UserAutoBuyMenu.java) - `telegram/menu/start/user/params/autobuy/UserAutoBuyMenu.java`
- [UserAutoBuyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/UserAutoBuyState.java) - `telegram/menu/start/user/params/autobuy/UserAutoBuyState.java`
- [AbstractWordsChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/AbstractWordsChooseState.java) - `telegram/menu/start/user/params/autobuy/words/AbstractWordsChooseState.java`
- [WordsAddChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/add/choose/WordsAddChooseState.java) - `telegram/menu/start/user/params/autobuy/words/add/choose/WordsAddChooseState.java`
- [WordsAddKeyWordState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/add/stage1/WordsAddKeyWordState.java) - `telegram/menu/start/user/params/autobuy/words/add/stage1/WordsAddKeyWordState.java`
- [WordsAddProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/add/stagep/WordsAddProceedState.java) - `telegram/menu/start/user/params/autobuy/words/add/stagep/WordsAddProceedState.java`
- [WordsAddData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/add/WordsAddData.java) - `telegram/menu/start/user/params/autobuy/words/add/WordsAddData.java`
- [WordsAddRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/add/WordsAddRegistry.java) - `telegram/menu/start/user/params/autobuy/words/add/WordsAddRegistry.java`
- [WordsDeleteChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/delete/choose/WordsDeleteChooseState.java) - `telegram/menu/start/user/params/autobuy/words/delete/choose/WordsDeleteChooseState.java`
- [WordsDeleteIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/delete/stage1/WordsDeleteIdState.java) - `telegram/menu/start/user/params/autobuy/words/delete/stage1/WordsDeleteIdState.java`
- [WordsDeleteProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/delete/stagep/WordsDeleteProceedState.java) - `telegram/menu/start/user/params/autobuy/words/delete/stagep/WordsDeleteProceedState.java`
- [WordsDeleteData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/delete/WordsDeleteData.java) - `telegram/menu/start/user/params/autobuy/words/delete/WordsDeleteData.java`
- [WordsDeleteRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/delete/WordsDeleteRegistry.java) - `telegram/menu/start/user/params/autobuy/words/delete/WordsDeleteRegistry.java`
- [WordsDeleteAllChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/deleteall/choose/WordsDeleteAllChooseState.java) - `telegram/menu/start/user/params/autobuy/words/deleteall/choose/WordsDeleteAllChooseState.java`
- [WordsDeleteAllProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/deleteall/stagep/WordsDeleteAllProceedState.java) - `telegram/menu/start/user/params/autobuy/words/deleteall/stagep/WordsDeleteAllProceedState.java`
- [WordsDeleteAllData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/deleteall/WordsDeleteAllData.java) - `telegram/menu/start/user/params/autobuy/words/deleteall/WordsDeleteAllData.java`
- [WordsDeleteAllRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/deleteall/WordsDeleteAllRegistry.java) - `telegram/menu/start/user/params/autobuy/words/deleteall/WordsDeleteAllRegistry.java`
- [WordsGetChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/get/choose/WordsGetChooseState.java) - `telegram/menu/start/user/params/autobuy/words/get/choose/WordsGetChooseState.java`
- [WordsGetProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/get/stagep/WordsGetProceedState.java) - `telegram/menu/start/user/params/autobuy/words/get/stagep/WordsGetProceedState.java`
- [WordsGetData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/get/WordsGetData.java) - `telegram/menu/start/user/params/autobuy/words/get/WordsGetData.java`
- [WordsGetRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/get/WordsGetRegistry.java) - `telegram/menu/start/user/params/autobuy/words/get/WordsGetRegistry.java`
- [UserWordsMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/UserWordsMenu.java) - `telegram/menu/start/user/params/autobuy/words/UserWordsMenu.java`
- [UserWordsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/UserWordsState.java) - `telegram/menu/start/user/params/autobuy/words/UserWordsState.java`
- [WordsType.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autobuy/words/WordsType.java) - `telegram/menu/start/user/params/autobuy/words/WordsType.java`
- [SwitchEvalModeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/asswitch/evalmode/SwitchEvalModeState.java) - `telegram/menu/start/user/params/autosell/asswitch/evalmode/SwitchEvalModeState.java`
- [SwitchEvalS1.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/asswitch/evals1/SwitchEvalS1.java) - `telegram/menu/start/user/params/autosell/asswitch/evals1/SwitchEvalS1.java`
- [AutoSellUpdateFieldState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/update/stage1/AutoSellUpdateFieldState.java) - `telegram/menu/start/user/params/autosell/update/stage1/AutoSellUpdateFieldState.java`
- [AutoSellUpdateValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/update/stage2/AutoSellUpdateValueState.java) - `telegram/menu/start/user/params/autosell/update/stage2/AutoSellUpdateValueState.java`
- [AutoSellUpdateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/update/stagep/AutoSellUpdateProceedState.java) - `telegram/menu/start/user/params/autosell/update/stagep/AutoSellUpdateProceedState.java`
- [UserAutoSellUpdateData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/update/UserAutoSellUpdateData.java) - `telegram/menu/start/user/params/autosell/update/UserAutoSellUpdateData.java`
- [UserAutoSellUpdateRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/update/UserAutoSellUpdateRegistry.java) - `telegram/menu/start/user/params/autosell/update/UserAutoSellUpdateRegistry.java`
- [UserAutoSellMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/UserAutoSellMenu.java) - `telegram/menu/start/user/params/autosell/UserAutoSellMenu.java`
- [UserAutoSellState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/autosell/UserAutoSellState.java) - `telegram/menu/start/user/params/autosell/UserAutoSellState.java`
- [ParamsCreateData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/ParamsCreateData.java) - `telegram/menu/start/user/params/create/ParamsCreateData.java`
- [ParamsCreateRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/ParamsCreateRegistry.java) - `telegram/menu/start/user/params/create/ParamsCreateRegistry.java`
- [AbstractCreateState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/prototype/AbstractCreateState.java) - `telegram/menu/start/user/params/create/prototype/AbstractCreateState.java`
- [CreateSourceState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/stage1/CreateSourceState.java) - `telegram/menu/start/user/params/create/stage1/CreateSourceState.java`
- [CreateDestinationState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/stage2/CreateDestinationState.java) - `telegram/menu/start/user/params/create/stage2/CreateDestinationState.java`
- [CreateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/create/stagep/CreateProceedState.java) - `telegram/menu/start/user/params/create/stagep/CreateProceedState.java`
- [ParamsDeleteData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/delete/ParamsDeleteData.java) - `telegram/menu/start/user/params/delete/ParamsDeleteData.java`
- [ParamsDeleteRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/delete/ParamsDeleteRegistry.java) - `telegram/menu/start/user/params/delete/ParamsDeleteRegistry.java`
- [DeleteRequestMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/delete/stage1/DeleteRequestMenu.java) - `telegram/menu/start/user/params/delete/stage1/DeleteRequestMenu.java`
- [DeleteRequestState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/delete/stage1/DeleteRequestState.java) - `telegram/menu/start/user/params/delete/stage1/DeleteRequestState.java`
- [DeleteProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/delete/stagep/DeleteProceedState.java) - `telegram/menu/start/user/params/delete/stagep/DeleteProceedState.java`
- [UserFollowCheckMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stage1/UserFollowCheckMenu.java) - `telegram/menu/start/user/params/follow/check/stage1/UserFollowCheckMenu.java`
- [UserFollowCheckState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stage1/UserFollowCheckState.java) - `telegram/menu/start/user/params/follow/check/stage1/UserFollowCheckState.java`
- [AbstractUserFollowProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stagep/AbstractUserFollowProceedState.java) - `telegram/menu/start/user/params/follow/check/stagep/AbstractUserFollowProceedState.java`
- [UserFollowAcceptState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stagep/UserFollowAcceptState.java) - `telegram/menu/start/user/params/follow/check/stagep/UserFollowAcceptState.java`
- [UserFollowDenyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/stagep/UserFollowDenyState.java) - `telegram/menu/start/user/params/follow/check/stagep/UserFollowDenyState.java`
- [UserFollowCheckData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/UserFollowCheckData.java) - `telegram/menu/start/user/params/follow/check/UserFollowCheckData.java`
- [UserFollowCheckRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/check/UserFollowCheckRegistry.java) - `telegram/menu/start/user/params/follow/check/UserFollowCheckRegistry.java`
- [FollowChooseState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/choose/FollowChooseState.java) - `telegram/menu/start/user/params/follow/follow/choose/FollowChooseState.java`
- [FollowIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/stage1/FollowIdState.java) - `telegram/menu/start/user/params/follow/follow/stage1/FollowIdState.java`
- [FollowPcoState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/stage2/FollowPcoState.java) - `telegram/menu/start/user/params/follow/follow/stage2/FollowPcoState.java`
- [FollowProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/stagep/FollowProceedState.java) - `telegram/menu/start/user/params/follow/follow/stagep/FollowProceedState.java`
- [UserFollowData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/UserFollowData.java) - `telegram/menu/start/user/params/follow/follow/UserFollowData.java`
- [UserFollowRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/follow/UserFollowRegistry.java) - `telegram/menu/start/user/params/follow/follow/UserFollowRegistry.java`
- [UnfollowIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/unfollow/stage1/UnfollowIdState.java) - `telegram/menu/start/user/params/follow/unfollow/stage1/UnfollowIdState.java`
- [UnfollowProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/unfollow/stagep/UnfollowProceedState.java) - `telegram/menu/start/user/params/follow/unfollow/stagep/UnfollowProceedState.java`
- [UserUnfollowData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/unfollow/UserUnfollowData.java) - `telegram/menu/start/user/params/follow/unfollow/UserUnfollowData.java`
- [UserUnfollowRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/unfollow/UserUnfollowRegistry.java) - `telegram/menu/start/user/params/follow/unfollow/UserUnfollowRegistry.java`
- [UserFollowMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/UserFollowMenu.java) - `telegram/menu/start/user/params/follow/UserFollowMenu.java`
- [UserFollowOperationType.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/UserFollowOperationType.java) - `telegram/menu/start/user/params/follow/UserFollowOperationType.java`
- [UserFollowState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/follow/UserFollowState.java) - `telegram/menu/start/user/params/follow/UserFollowState.java`
- [ParamsListState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/list/ParamsListState.java) - `telegram/menu/start/user/params/list/ParamsListState.java`
- [ParamsRenameIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/rename/stage1/ParamsRenameIdState.java) - `telegram/menu/start/user/params/rename/stage1/ParamsRenameIdState.java`
- [ParamsRenameValueState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/rename/stage2/ParamsRenameValueState.java) - `telegram/menu/start/user/params/rename/stage2/ParamsRenameValueState.java`
- [ParamsRenameProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/rename/stagep/ParamsRenameProceedState.java) - `telegram/menu/start/user/params/rename/stagep/ParamsRenameProceedState.java`
- [UserParamsRenameRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/rename/UserParamsRenameRegistry.java) - `telegram/menu/start/user/params/rename/UserParamsRenameRegistry.java`
- [UserRenameData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/rename/UserRenameData.java) - `telegram/menu/start/user/params/rename/UserRenameData.java`
- [UserDeepParamsMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/UserDeepParamsMenu.java) - `telegram/menu/start/user/params/UserDeepParamsMenu.java`
- [UserDeepParamsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/params/UserDeepParamsState.java) - `telegram/menu/start/user/params/UserDeepParamsState.java`
- [ClassicTableMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/ClassicTableMenu.java) - `telegram/menu/start/user/portfolio/ClassicTableMenu.java`
- [ITableGenerator.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/ITableGenerator.java) - `telegram/menu/start/user/portfolio/ITableGenerator.java`
- [UserTableMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/UserTableMenu.java) - `telegram/menu/start/user/portfolio/UserTableMenu.java`
- [UserTableState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/UserTableState.java) - `telegram/menu/start/user/portfolio/UserTableState.java`
- [TableHistoryModeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/choose/TableHistoryModeState.java) - `telegram/menu/start/user/portfolio/v1/history/choose/TableHistoryModeState.java`
- [TableHistoryPeriodState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stage1/TableHistoryPeriodState.java) - `telegram/menu/start/user/portfolio/v1/history/stage1/TableHistoryPeriodState.java`
- [AbstractTableHistoryGenerator.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stagep/generator/AbstractTableHistoryGenerator.java) - `telegram/menu/start/user/portfolio/v1/history/stagep/generator/AbstractTableHistoryGenerator.java`
- [TableBuyHistoryGenerator.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stagep/generator/TableBuyHistoryGenerator.java) - `telegram/menu/start/user/portfolio/v1/history/stagep/generator/TableBuyHistoryGenerator.java`
- [TableSellHistoryGenerator.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stagep/generator/TableSellHistoryGenerator.java) - `telegram/menu/start/user/portfolio/v1/history/stagep/generator/TableSellHistoryGenerator.java`
- [AbstractHistoryProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stagep/parent/AbstractHistoryProceedState.java) - `telegram/menu/start/user/portfolio/v1/history/stagep/parent/AbstractHistoryProceedState.java`
- [TableBuyHistoryProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stagep/TableBuyHistoryProceedState.java) - `telegram/menu/start/user/portfolio/v1/history/stagep/TableBuyHistoryProceedState.java`
- [TableSellHistoryProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/stagep/TableSellHistoryProceedState.java) - `telegram/menu/start/user/portfolio/v1/history/stagep/TableSellHistoryProceedState.java`
- [TableHistoryData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/TableHistoryData.java) - `telegram/menu/start/user/portfolio/v1/history/TableHistoryData.java`
- [TableHistoryMode.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/TableHistoryMode.java) - `telegram/menu/start/user/portfolio/v1/history/TableHistoryMode.java`
- [TableHistoryRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/history/TableHistoryRegistry.java) - `telegram/menu/start/user/portfolio/v1/history/TableHistoryRegistry.java`
- [TableWaitingGenerator.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/waiting/generator/TableWaitingGenerator.java) - `telegram/menu/start/user/portfolio/v1/waiting/generator/TableWaitingGenerator.java`
- [TableWaitingState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v1/waiting/TableWaitingState.java) - `telegram/menu/start/user/portfolio/v1/waiting/TableWaitingState.java`
- [TableV2InventoryGenerator.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/inventory/stage1/generator/TableV2InventoryGenerator.java) - `telegram/menu/start/user/portfolio/v2/inventory/stage1/generator/TableV2InventoryGenerator.java`
- [TableV2InventoryListState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/inventory/stage1/TableV2InventoryListState.java) - `telegram/menu/start/user/portfolio/v2/inventory/stage1/TableV2InventoryListState.java`
- [TableV2InventoryProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/inventory/stagep/TableV2InventoryProceedState.java) - `telegram/menu/start/user/portfolio/v2/inventory/stagep/TableV2InventoryProceedState.java`
- [TableV2InventoryData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/inventory/TableV2InventoryData.java) - `telegram/menu/start/user/portfolio/v2/inventory/TableV2InventoryData.java`
- [TableV2InventoryRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/inventory/TableV2InventoryRegistry.java) - `telegram/menu/start/user/portfolio/v2/inventory/TableV2InventoryRegistry.java`
- [TableV2RestoreAgreementMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/restore/stage1/TableV2RestoreAgreementMenu.java) - `telegram/menu/start/user/portfolio/v2/restore/stage1/TableV2RestoreAgreementMenu.java`
- [TableV2RestoreAgreementState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/restore/stage1/TableV2RestoreAgreementState.java) - `telegram/menu/start/user/portfolio/v2/restore/stage1/TableV2RestoreAgreementState.java`
- [TableV2RestoreProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/restore/stagep/TableV2RestoreProceedState.java) - `telegram/menu/start/user/portfolio/v2/restore/stagep/TableV2RestoreProceedState.java`
- [TableV2SellingGenerator.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/selling/stage1/generator/TableV2SellingGenerator.java) - `telegram/menu/start/user/portfolio/v2/selling/stage1/generator/TableV2SellingGenerator.java`
- [TableV2SellingListState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/selling/stage1/TableV2SellingListState.java) - `telegram/menu/start/user/portfolio/v2/selling/stage1/TableV2SellingListState.java`
- [TableV2SellingProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/selling/stagep/TableV2SellingProceedState.java) - `telegram/menu/start/user/portfolio/v2/selling/stagep/TableV2SellingProceedState.java`
- [TableV2SellingData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/selling/TableV2SellingData.java) - `telegram/menu/start/user/portfolio/v2/selling/TableV2SellingData.java`
- [TableV2SellingRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/v2/selling/TableV2SellingRegistry.java) - `telegram/menu/start/user/portfolio/v2/selling/TableV2SellingRegistry.java`
- [YTPTableState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/portfolio/YTPTableState.java) - `telegram/menu/start/user/portfolio/YTPTableState.java`
- [UserPreferencesMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/preferences/UserPreferencesMenu.java) - `telegram/menu/start/user/preferences/UserPreferencesMenu.java`
- [UserPreferencesState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/preferences/UserPreferencesState.java) - `telegram/menu/start/user/preferences/UserPreferencesState.java`
- [ParamsSwitchData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/pswitch/ParamsSwitchData.java) - `telegram/menu/start/user/pswitch/ParamsSwitchData.java`
- [ParamsSwitchRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/pswitch/ParamsSwitchRegistry.java) - `telegram/menu/start/user/pswitch/ParamsSwitchRegistry.java`
- [ParamsSwitchIdState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/pswitch/stage1/ParamsSwitchIdState.java) - `telegram/menu/start/user/pswitch/stage1/ParamsSwitchIdState.java`
- [ParamsSwitchProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/pswitch/stagep/ParamsSwitchProceedState.java) - `telegram/menu/start/user/pswitch/stagep/ParamsSwitchProceedState.java`
- [QuickConfigDisableState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/disable/QuickConfigDisableState.java) - `telegram/menu/start/user/quick/disable/QuickConfigDisableState.java`
- [AbstractQuickConfigGradeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/prototype/AbstractQuickConfigGradeState.java) - `telegram/menu/start/user/quick/enable/prototype/AbstractQuickConfigGradeState.java`
- [QuickConfigGradeMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/prototype/QuickConfigGradeMenu.java) - `telegram/menu/start/user/quick/enable/prototype/QuickConfigGradeMenu.java`
- [QuickConfigCreateData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/QuickConfigCreateData.java) - `telegram/menu/start/user/quick/enable/QuickConfigCreateData.java`
- [QuickConfigCreateRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/QuickConfigCreateRegistry.java) - `telegram/menu/start/user/quick/enable/QuickConfigCreateRegistry.java`
- [ConfigCreateAutoBuyState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stage1/ConfigCreateAutoBuyState.java) - `telegram/menu/start/user/quick/enable/stage1/ConfigCreateAutoBuyState.java`
- [ConfigCreateAutoSellState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stage2/ConfigCreateAutoSellState.java) - `telegram/menu/start/user/quick/enable/stage2/ConfigCreateAutoSellState.java`
- [ConfigCreateBankSizeState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stage3/ConfigCreateBankSizeState.java) - `telegram/menu/start/user/quick/enable/stage3/ConfigCreateBankSizeState.java`
- [ConfigCreateProceedState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/quick/enable/stagep/ConfigCreateProceedState.java) - `telegram/menu/start/user/quick/enable/stagep/ConfigCreateProceedState.java`
- [UserParamsMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/UserParamsMenu.java) - `telegram/menu/start/user/UserParamsMenu.java`
- [UserParamsState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/user/UserParamsState.java) - `telegram/menu/start/user/UserParamsState.java`
- [UserStartMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/UserStartMenu.java) - `telegram/menu/start/UserStartMenu.java`
- [UserStartState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/start/UserStartState.java) - `telegram/menu/start/UserStartState.java`
- [UserMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu/UserMenu.java) - `telegram/menu/UserMenu.java`
- [BotCommandProvider.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/messaging/BotCommandProvider.java) - `telegram/messaging/BotCommandProvider.java`
- [UserStateData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/messaging/dto/UserStateData.java) - `telegram/messaging/dto/UserStateData.java`
- [MessageInfoDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/messaging/MessageInfoDto.java) - `telegram/messaging/MessageInfoDto.java`
- [MessageType.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/messaging/MessageType.java) - `telegram/messaging/MessageType.java`
- [TelegramUpdReceiverService.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/messaging/receiver/TelegramUpdReceiverService.java) - `telegram/messaging/receiver/TelegramUpdReceiverService.java`
- [YTNotificationReceiverService.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/messaging/receiver/YTNotificationReceiverService.java) - `telegram/messaging/receiver/YTNotificationReceiverService.java`
- [NotificationRedisConsumerService.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/messaging/redis/NotificationRedisConsumerService.java) - `telegram/messaging/redis/NotificationRedisConsumerService.java`
- [TelegramRedisConsumerService.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/messaging/redis/TelegramRedisConsumerService.java) - `telegram/messaging/redis/TelegramRedisConsumerService.java`
- [TelegramConfiguration.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/messaging/TelegramConfiguration.java) - `telegram/messaging/TelegramConfiguration.java`
- [TelegramSenderConfiguration.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/messaging/TelegramSenderConfiguration.java) - `telegram/messaging/TelegramSenderConfiguration.java`
- [UserData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/data/UserData.java) - `telegram/prototype/data/UserData.java`
- [ErrorMessageGenerator.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/ErrorMessageGenerator.java) - `telegram/prototype/ErrorMessageGenerator.java`
- [YTPDocMenuState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/menu/doc/base/YTPDocMenuState.java) - `telegram/prototype/menu/doc/base/YTPDocMenuState.java`
- [YTPTerminalDocMenuState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/menu/doc/YTPTerminalDocMenuState.java) - `telegram/prototype/menu/doc/YTPTerminalDocMenuState.java`
- [YTPImageMenuState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/menu/img/YTPImageMenuState.java) - `telegram/prototype/menu/img/YTPImageMenuState.java`
- [PageMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/menu/PageMenu.java) - `telegram/prototype/menu/PageMenu.java`
- [TerminalMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/menu/TerminalMenu.java) - `telegram/prototype/menu/TerminalMenu.java`
- [TerminalMenuInt.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/menu/TerminalMenuInt.java) - `telegram/prototype/menu/TerminalMenuInt.java`
- [AbstractPcoTextMenuState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/menu/text/AbstractPcoTextMenuState.java) - `telegram/prototype/menu/text/AbstractPcoTextMenuState.java`
- [YTPTextMenuState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/menu/text/base/YTPTextMenuState.java) - `telegram/prototype/menu/text/base/YTPTextMenuState.java`
- [YTPTextState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/menu/text/base/YTPTextState.java) - `telegram/prototype/menu/text/base/YTPTextState.java`
- [YTPPageTextMenuState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/menu/text/YTPPageTextMenuState.java) - `telegram/prototype/menu/text/YTPPageTextMenuState.java`
- [YTPTerminalTextMenuState.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/menu/text/YTPTerminalTextMenuState.java) - `telegram/prototype/menu/text/YTPTerminalTextMenuState.java`
- [YTNotificationMenu.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/notification/YTNotificationMenu.java) - `telegram/prototype/notification/YTNotificationMenu.java`
- [YTTextNotifier.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/notification/YTTextNotifier.java) - `telegram/prototype/notification/YTTextNotifier.java`
- [UserDocMessageSender.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/sender/doc/UserDocMessageSender.java) - `telegram/prototype/sender/doc/UserDocMessageSender.java`
- [UserImageMessageSender.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/sender/image/UserImageMessageSender.java) - `telegram/prototype/sender/image/UserImageMessageSender.java`
- [UserTextMessageSender.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/sender/text/UserTextMessageSender.java) - `telegram/prototype/sender/text/UserTextMessageSender.java`
- [StateRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/StateRegistry.java) - `telegram/prototype/StateRegistry.java`
- [UserInitializer.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/UserInitializer.java) - `telegram/prototype/UserInitializer.java`
- [UserRegistry.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/prototype/UserRegistry.java) - `telegram/prototype/UserRegistry.java`
- [ChangeNameOption.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/ChangeNameOption.java) - `util/autotrade/ChangeNameOption.java`
- [AbstractFcdDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/AbstractFcdDto.java) - `util/autotrade/dto/AbstractFcdDto.java`
- [FcdAdminDeleteDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/admin/FcdAdminDeleteDto.java) - `util/autotrade/dto/admin/FcdAdminDeleteDto.java`
- [FcdAdminGiveBalanceDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/admin/FcdAdminGiveBalanceDto.java) - `util/autotrade/dto/admin/FcdAdminGiveBalanceDto.java`
- [FcdAdminRoleDataDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/admin/FcdAdminRoleDataDto.java) - `util/autotrade/dto/admin/FcdAdminRoleDataDto.java`
- [FcdAdminUserDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/admin/FcdAdminUserDto.java) - `util/autotrade/dto/admin/FcdAdminUserDto.java`
- [FcdAdminUserRequestDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/admin/FcdAdminUserRequestDto.java) - `util/autotrade/dto/admin/FcdAdminUserRequestDto.java`
- [BuyTokenAddDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/BuyTokenAddDto.java) - `util/autotrade/dto/BuyTokenAddDto.java`
- [DeleteAnsDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/DeleteAnsDto.java) - `util/autotrade/dto/DeleteAnsDto.java`
- [FcdDefaultDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/FcdDefaultDto.java) - `util/autotrade/dto/FcdDefaultDto.java`
- [FcdDtoIfc.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/FcdDtoIfc.java) - `util/autotrade/dto/FcdDtoIfc.java`
- [FcdNewestScoringData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/FcdNewestScoringData.java) - `util/autotrade/dto/FcdNewestScoringData.java`
- [ItemStatsSummaryDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/ItemStatsSummaryDto.java) - `util/autotrade/dto/ItemStatsSummaryDto.java`
- [FcdGetPricesDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/norole/FcdGetPricesDto.java) - `util/autotrade/dto/norole/FcdGetPricesDto.java`
- [FcdTopUpDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/norole/FcdTopUpDto.java) - `util/autotrade/dto/norole/FcdTopUpDto.java`
- [WorkerPriceData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/norole/WorkerPriceData.java) - `util/autotrade/dto/norole/WorkerPriceData.java`
- [ParamsAddDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/ParamsAddDto.java) - `util/autotrade/dto/ParamsAddDto.java`
- [SellTokenAddDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/SellTokenAddDto.java) - `util/autotrade/dto/SellTokenAddDto.java`
- [FcdAccountsPageV2Dto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/accounts/FcdAccountsPageV2Dto.java) - `util/autotrade/dto/user/accounts/FcdAccountsPageV2Dto.java`
- [FcdAccountsTransferDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/accounts/FcdAccountsTransferDto.java) - `util/autotrade/dto/user/accounts/FcdAccountsTransferDto.java`
- [FcdAccountsV2Dto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/accounts/FcdAccountsV2Dto.java) - `util/autotrade/dto/user/accounts/FcdAccountsV2Dto.java`
- [FcdAccountTransferResultDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/accounts/FcdAccountTransferResultDto.java) - `util/autotrade/dto/user/accounts/FcdAccountTransferResultDto.java`
- [FcdAccountTransferStatus.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/accounts/FcdAccountTransferStatus.java) - `util/autotrade/dto/user/accounts/FcdAccountTransferStatus.java`
- [FcdCodeAnsDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/accounts/FcdCodeAnsDto.java) - `util/autotrade/dto/user/accounts/FcdCodeAnsDto.java`
- [FcdCodeBulkAnswer.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/accounts/FcdCodeBulkAnswer.java) - `util/autotrade/dto/user/accounts/FcdCodeBulkAnswer.java`
- [FcdScoringUpdateDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/buy/FcdScoringUpdateDto.java) - `util/autotrade/dto/user/buy/FcdScoringUpdateDto.java`
- [FcdWordsAddDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/buy/FcdWordsAddDto.java) - `util/autotrade/dto/user/buy/FcdWordsAddDto.java`
- [FcdGeneralAccInfoDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/general/FcdGeneralAccInfoDto.java) - `util/autotrade/dto/user/general/FcdGeneralAccInfoDto.java`
- [FcdGeneralNewestDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/general/FcdGeneralNewestDto.java) - `util/autotrade/dto/user/general/FcdGeneralNewestDto.java`
- [FcdTokenGetSingleDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/general/FcdTokenGetSingleDto.java) - `util/autotrade/dto/user/general/FcdTokenGetSingleDto.java`
- [FcdParamsCopyReqCallbackDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsCopyReqCallbackDto.java) - `util/autotrade/dto/user/params/FcdParamsCopyReqCallbackDto.java`
- [FcdParamsCopyReqDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsCopyReqDto.java) - `util/autotrade/dto/user/params/FcdParamsCopyReqDto.java`
- [FcdParamsCopyResDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsCopyResDto.java) - `util/autotrade/dto/user/params/FcdParamsCopyResDto.java`
- [FcdParamsDeleteReqDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsDeleteReqDto.java) - `util/autotrade/dto/user/params/FcdParamsDeleteReqDto.java`
- [FcdParamsDeleteResDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsDeleteResDto.java) - `util/autotrade/dto/user/params/FcdParamsDeleteResDto.java`
- [FcdParamsFollowDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsFollowDto.java) - `util/autotrade/dto/user/params/FcdParamsFollowDto.java`
- [FcdParamsGetDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsGetDto.java) - `util/autotrade/dto/user/params/FcdParamsGetDto.java`
- [FcdParamsGetScoringDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsGetScoringDto.java) - `util/autotrade/dto/user/params/FcdParamsGetScoringDto.java`
- [FcdParamsGetWordDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsGetWordDto.java) - `util/autotrade/dto/user/params/FcdParamsGetWordDto.java`
- [FcdParamsListDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsListDto.java) - `util/autotrade/dto/user/params/FcdParamsListDto.java`
- [FcdParamsQCData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsQCData.java) - `util/autotrade/dto/user/params/FcdParamsQCData.java`
- [FcdParamsQuickConfigInitDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsQuickConfigInitDto.java) - `util/autotrade/dto/user/params/FcdParamsQuickConfigInitDto.java`
- [FcdParamsSwitchDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/params/FcdParamsSwitchDto.java) - `util/autotrade/dto/user/params/FcdParamsSwitchDto.java`
- [FcdRefDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/ref/FcdRefDto.java) - `util/autotrade/dto/user/ref/FcdRefDto.java`
- [FcdSellChangeGetDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/change/FcdSellChangeGetDto.java) - `util/autotrade/dto/user/sell/change/FcdSellChangeGetDto.java`
- [FcdSellChangePostDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/change/FcdSellChangePostDto.java) - `util/autotrade/dto/user/sell/change/FcdSellChangePostDto.java`
- [FcdSellTokensAddDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/FcdSellTokensAddDto.java) - `util/autotrade/dto/user/sell/FcdSellTokensAddDto.java`
- [FcdBuyHistoryDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/history/buy/FcdBuyHistoryDto.java) - `util/autotrade/dto/user/sell/history/buy/FcdBuyHistoryDto.java`
- [FcdBuyHistoryFullDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/history/buy/FcdBuyHistoryFullDto.java) - `util/autotrade/dto/user/sell/history/buy/FcdBuyHistoryFullDto.java`
- [FcdSellHistoryDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/history/sell/FcdSellHistoryDto.java) - `util/autotrade/dto/user/sell/history/sell/FcdSellHistoryDto.java`
- [FcdSellHistoryFullDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/history/sell/FcdSellHistoryFullDto.java) - `util/autotrade/dto/user/sell/history/sell/FcdSellHistoryFullDto.java`
- [FcdSellListGetDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/list/FcdSellListGetDto.java) - `util/autotrade/dto/user/sell/list/FcdSellListGetDto.java`
- [FcdSellListGetFullDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/list/FcdSellListGetFullDto.java) - `util/autotrade/dto/user/sell/list/FcdSellListGetFullDto.java`
- [FcdSellListPostDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/list/FcdSellListPostDto.java) - `util/autotrade/dto/user/sell/list/FcdSellListPostDto.java`
- [AbstractFcdPortfolioV2Data.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/parent/AbstractFcdPortfolioV2Data.java) - `util/autotrade/dto/user/sell/parent/AbstractFcdPortfolioV2Data.java`
- [AbstrFcdSellGetFullCommand.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/parent/AbstrFcdSellGetFullCommand.java) - `util/autotrade/dto/user/sell/parent/AbstrFcdSellGetFullCommand.java`
- [AbstrFcdSellGetSingleCommand.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/parent/AbstrFcdSellGetSingleCommand.java) - `util/autotrade/dto/user/sell/parent/AbstrFcdSellGetSingleCommand.java`
- [FcdSellRestrictGetDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/restrict/FcdSellRestrictGetDto.java) - `util/autotrade/dto/user/sell/restrict/FcdSellRestrictGetDto.java`
- [FcdSellRestrictPostDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/restrict/FcdSellRestrictPostDto.java) - `util/autotrade/dto/user/sell/restrict/FcdSellRestrictPostDto.java`
- [FcdSellUploadDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/upload/FcdSellUploadDto.java) - `util/autotrade/dto/user/sell/upload/FcdSellUploadDto.java`
- [FcdSellUploadGetDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/upload/FcdSellUploadGetDto.java) - `util/autotrade/dto/user/sell/upload/FcdSellUploadGetDto.java`
- [FcdSellUploadGroupDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/upload/FcdSellUploadGroupDto.java) - `util/autotrade/dto/user/sell/upload/FcdSellUploadGroupDto.java`
- [FcdSellUploadInfoDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/upload/FcdSellUploadInfoDto.java) - `util/autotrade/dto/user/sell/upload/FcdSellUploadInfoDto.java`
- [FcdInvV2GetDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/v2/inventory/FcdInvV2GetDto.java) - `util/autotrade/dto/user/sell/v2/inventory/FcdInvV2GetDto.java`
- [FcdInvV2ItemDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/v2/inventory/FcdInvV2ItemDto.java) - `util/autotrade/dto/user/sell/v2/inventory/FcdInvV2ItemDto.java`
- [FcdInvV2PostDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/v2/inventory/FcdInvV2PostDto.java) - `util/autotrade/dto/user/sell/v2/inventory/FcdInvV2PostDto.java`
- [FcdInvV2PostGroupDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/v2/inventory/FcdInvV2PostGroupDto.java) - `util/autotrade/dto/user/sell/v2/inventory/FcdInvV2PostGroupDto.java`
- [FcdSellingV2PostDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/v2/selling/FcdSellingV2PostDto.java) - `util/autotrade/dto/user/sell/v2/selling/FcdSellingV2PostDto.java`
- [FcdSellingV2PostGroupDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/v2/selling/FcdSellingV2PostGroupDto.java) - `util/autotrade/dto/user/sell/v2/selling/FcdSellingV2PostGroupDto.java`
- [FcdSellWaitDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/wait/FcdSellWaitDto.java) - `util/autotrade/dto/user/sell/wait/FcdSellWaitDto.java`
- [FcdSellWaitFullDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/user/sell/wait/FcdSellWaitFullDto.java) - `util/autotrade/dto/user/sell/wait/FcdSellWaitFullDto.java`
- [WordDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/dto/WordDto.java) - `util/autotrade/dto/WordDto.java`
- [DuplicateMode.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/DuplicateMode.java) - `util/autotrade/DuplicateMode.java`
- [AtAdminEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/admin/AtAdminEndpoint.java) - `util/autotrade/endpoint/admin/AtAdminEndpoint.java`
- [NoRoleAdminAddEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/norole/NoRoleAdminAddEndpoint.java) - `util/autotrade/endpoint/norole/NoRoleAdminAddEndpoint.java`
- [NoRoleChatAddEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/norole/NoRoleChatAddEndpoint.java) - `util/autotrade/endpoint/norole/NoRoleChatAddEndpoint.java`
- [SubGetEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/norole/SubGetEndpoint.java) - `util/autotrade/endpoint/norole/SubGetEndpoint.java`
- [AbstractAtEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/parent/AbstractAtEndpoint.java) - `util/autotrade/endpoint/parent/AbstractAtEndpoint.java`
- [AbstractAtNoRoleAddEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/parent/AbstractAtNoRoleAddEndpoint.java) - `util/autotrade/endpoint/parent/AbstractAtNoRoleAddEndpoint.java`
- [AbstractAtWordsEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/parent/AbstractAtWordsEndpoint.java) - `util/autotrade/endpoint/parent/AbstractAtWordsEndpoint.java`
- [AtCommunicationInt.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/parent/AtCommunicationInt.java) - `util/autotrade/endpoint/parent/AtCommunicationInt.java`
- [AccountsV2Endpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/accounts/AccountsV2Endpoint.java) - `util/autotrade/endpoint/user/accounts/AccountsV2Endpoint.java`
- [FcdAccountsTransferInput.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/accounts/dto/FcdAccountsTransferInput.java) - `util/autotrade/endpoint/user/accounts/dto/FcdAccountsTransferInput.java`
- [BuyEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/buy/BuyEndpoint.java) - `util/autotrade/endpoint/user/buy/BuyEndpoint.java`
- [ExcludedWordsEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/buy/dicts/ExcludedWordsEndpoint.java) - `util/autotrade/endpoint/user/buy/dicts/ExcludedWordsEndpoint.java`
- [IncludedWordsEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/buy/dicts/IncludedWordsEndpoint.java) - `util/autotrade/endpoint/user/buy/dicts/IncludedWordsEndpoint.java`
- [ScoringEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/buy/scoring/ScoringEndpoint.java) - `util/autotrade/endpoint/user/buy/scoring/ScoringEndpoint.java`
- [GeneralEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/general/GeneralEndpoint.java) - `util/autotrade/endpoint/user/general/GeneralEndpoint.java`
- [ParamsEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/params/ParamsEndpoint.java) - `util/autotrade/endpoint/user/params/ParamsEndpoint.java`
- [ParamsQuickConfigEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/params/ParamsQuickConfigEndpoint.java) - `util/autotrade/endpoint/user/params/ParamsQuickConfigEndpoint.java`
- [PropertiesEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/properties/PropertiesEndpoint.java) - `util/autotrade/endpoint/user/properties/PropertiesEndpoint.java`
- [RefEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/ref/RefEndpoint.java) - `util/autotrade/endpoint/user/ref/RefEndpoint.java`
- [SellDefaultEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/sell/SellDefaultEndpoint.java) - `util/autotrade/endpoint/user/sell/SellDefaultEndpoint.java`
- [SellTokensAddEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/sell/SellTokensAddEndpoint.java) - `util/autotrade/endpoint/user/sell/SellTokensAddEndpoint.java`
- [SellV2InventoryEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/sell/v2/SellV2InventoryEndpoint.java) - `util/autotrade/endpoint/user/sell/v2/SellV2InventoryEndpoint.java`
- [SellV2RestoreEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/sell/v2/SellV2RestoreEndpoint.java) - `util/autotrade/endpoint/user/sell/v2/SellV2RestoreEndpoint.java`
- [SellV2SellingEndpoint.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoint/user/sell/v2/SellV2SellingEndpoint.java) - `util/autotrade/endpoint/user/sell/v2/SellV2SellingEndpoint.java`
- [FcdDistance.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/FcdDistance.java) - `util/autotrade/FcdDistance.java`
- [FcdStringUtils.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/FcdStringUtils.java) - `util/autotrade/FcdStringUtils.java`
- [FunctionType.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/FunctionType.java) - `util/autotrade/FunctionType.java`
- [ItemScoringType.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/ItemScoringType.java) - `util/autotrade/ItemScoringType.java`
- [MaFileStatus.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/MaFileStatus.java) - `util/autotrade/MaFileStatus.java`
- [MarketType.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/MarketType.java) - `util/autotrade/MarketType.java`
- [ParamsCopyOptions.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/ParamsCopyOptions.java) - `util/autotrade/ParamsCopyOptions.java`
- [QuickConfigGrade.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/QuickConfigGrade.java) - `util/autotrade/QuickConfigGrade.java`
- [RoleName.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/RoleName.java) - `util/autotrade/RoleName.java`
- [SellPriceEvalMode.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/SellPriceEvalMode.java) - `util/autotrade/SellPriceEvalMode.java`
- [TdpCopyMainData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/TdpCopyMainData.java) - `util/autotrade/TdpCopyMainData.java`
- [TdpField.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/TdpField.java) - `util/autotrade/TdpField.java`
- [TopUpType.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/TopUpType.java) - `util/autotrade/TopUpType.java`
- [AccountData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/util/accounts/AccountData.java) - `util/autotrade/util/accounts/AccountData.java`
- [FcdAccountV2Dto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/util/accounts/FcdAccountV2Dto.java) - `util/autotrade/util/accounts/FcdAccountV2Dto.java`
- [MaFileTokenAddInput.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/util/accounts/MaFileTokenAddInput.java) - `util/autotrade/util/accounts/MaFileTokenAddInput.java`
- [WorkerData.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/util/accounts/WorkerData.java) - `util/autotrade/util/accounts/WorkerData.java`
- [ItemsWithoutPricesWrapped.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/util/ItemsWithoutPricesWrapped.java) - `util/autotrade/util/ItemsWithoutPricesWrapped.java`
- [ErrorMessageDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/util/parent/ErrorMessageDto.java) - `util/autotrade/util/parent/ErrorMessageDto.java`
- [PriceIntervalUpdateV2Dto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/util/PriceIntervalUpdateV2Dto.java) - `util/autotrade/util/PriceIntervalUpdateV2Dto.java`
- [YouTradeOnSellItemMainInfoDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/util/YouTradeOnSellItemMainInfoDto.java) - `util/autotrade/util/YouTradeOnSellItemMainInfoDto.java`
- [YouTradePurchasedHistoryDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/util/YouTradePurchasedHistoryDto.java) - `util/autotrade/util/YouTradePurchasedHistoryDto.java`
- [YouTradeSoldItemMainInfoDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/util/YouTradeSoldItemMainInfoDto.java) - `util/autotrade/util/YouTradeSoldItemMainInfoDto.java`
- [YouTradeWaitingItemMainInfoDto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/util/YouTradeWaitingItemMainInfoDto.java) - `util/autotrade/util/YouTradeWaitingItemMainInfoDto.java`
- [YdpField.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/YdpField.java) - `util/autotrade/YdpField.java`
- [CustomHibernatePropertiesConfig.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/datasource/CustomHibernatePropertiesConfig.java) - `util/datasource/CustomHibernatePropertiesConfig.java`
- [KeyGenDataSourceConfigurer.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/datasource/KeyGenDataSourceConfigurer.java) - `util/datasource/KeyGenDataSourceConfigurer.java`
- [DynamicEmoji.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/emoji/DynamicEmoji.java) - `util/emoji/DynamicEmoji.java`
- [ColorUtils.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/excel/ColorUtils.java) - `util/excel/ColorUtils.java`
- [ExcelExclude.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/excel/ExcelExclude.java) - `util/excel/ExcelExclude.java`
- [ExcelSeparator.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/excel/ExcelSeparator.java) - `util/excel/ExcelSeparator.java`
- [AbstractXlsxGenerator.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/excel/generator/AbstractXlsxGenerator.java) - `util/excel/generator/AbstractXlsxGenerator.java`
- [NewestItemsXlsxGenerator.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/excel/generator/NewestItemsXlsxGenerator.java) - `util/excel/generator/NewestItemsXlsxGenerator.java`
- [XlsxExporter.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/excel/XlsxExporter.java) - `util/excel/XlsxExporter.java`
- [XlsxParserHelper.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/excel/XlsxParserHelper.java) - `util/excel/XlsxParserHelper.java`
- [UpdateTypeAdapter.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/gson/UpdateTypeAdapter.java) - `util/gson/UpdateTypeAdapter.java`
- [MinIODto.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/minio/dto/MinIODto.java) - `util/minio/dto/MinIODto.java`
- [MinIOInputStream.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/minio/dto/MinIOInputStream.java) - `util/minio/dto/MinIOInputStream.java`
- [MinIOFileDownloadService.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/minio/MinIOFileDownloadService.java) - `util/minio/MinIOFileDownloadService.java`
- [BargainFailureReason.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/BargainFailureReason.java) - `util/notification/BargainFailureReason.java`
- [YTBargainNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/buy/YTBargainNotification.java) - `util/notification/buy/YTBargainNotification.java`
- [YTBuyCompletedNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/buy/YTBuyCompletedNotification.java) - `util/notification/buy/YTBuyCompletedNotification.java`
- [YTBuyFailedNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/buy/YTBuyFailedNotification.java) - `util/notification/buy/YTBuyFailedNotification.java`
- [YTWaitNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/general/YTWaitNotification.java) - `util/notification/general/YTWaitNotification.java`
- [YTMaFileDeleteNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/mafile/YTMaFileDeleteNotification.java) - `util/notification/mafile/YTMaFileDeleteNotification.java`
- [YTChangeItemAns.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/portfolio/YTChangeItemAns.java) - `util/notification/portfolio/YTChangeItemAns.java`
- [YTChangeNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/portfolio/YTChangeNotification.java) - `util/notification/portfolio/YTChangeNotification.java`
- [YTDeleteNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/portfolio/YTDeleteNotification.java) - `util/notification/portfolio/YTDeleteNotification.java`
- [YTInvBaseRestrictNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/portfolio/YTInvBaseRestrictNotification.java) - `util/notification/portfolio/YTInvBaseRestrictNotification.java`
- [YTInvUploadNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/portfolio/YTInvUploadNotification.java) - `util/notification/portfolio/YTInvUploadNotification.java`
- [ProductType.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/ProductType.java) - `util/notification/ProductType.java`
- [YTSellAddedNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/sell/YTSellAddedNotification.java) - `util/notification/sell/YTSellAddedNotification.java`
- [YTSellCompletedNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/sell/YTSellCompletedNotification.java) - `util/notification/sell/YTSellCompletedNotification.java`
- [YTSellFailedNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/sell/YTSellFailedNotification.java) - `util/notification/sell/YTSellFailedNotification.java`
- [YTAnyNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/YTAnyNotification.java) - `util/notification/YTAnyNotification.java`
- [YTBalanceNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/YTBalanceNotification.java) - `util/notification/YTBalanceNotification.java`
- [YTBaseNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/YTBaseNotification.java) - `util/notification/YTBaseNotification.java`
- [YTMessageNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/YTMessageNotification.java) - `util/notification/YTMessageNotification.java`
- [YTMessageType.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/YTMessageType.java) - `util/notification/YTMessageType.java`
- [YTNotificationType.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/YTNotificationType.java) - `util/notification/YTNotificationType.java`
- [YTPaymentNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/YTPaymentNotification.java) - `util/notification/YTPaymentNotification.java`
- [YTSkinNotification.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/notification/YTSkinNotification.java) - `util/notification/YTSkinNotification.java`
- [AbstractRedisConsumerService.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/redis/AbstractRedisConsumerService.java) - `util/redis/AbstractRedisConsumerService.java`
- [IRedisConsumer.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/redis/IRedisConsumer.java) - `util/redis/IRedisConsumer.java`
- [YouTradeColorCodes.java](../../telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/YouTradeColorCodes.java) - `util/YouTradeColorCodes.java`

## Resources

- [application.properties](../../telegram-bot/src/main/resources/application.properties)
- [add_to_sell.png](../../telegram-bot/src/main/resources/images/start/add_to_sell.png)
- [autobuy_settings.png](../../telegram-bot/src/main/resources/images/start/autobuy_settings.png)
- [autosell_settings.png](../../telegram-bot/src/main/resources/images/start/autosell_settings.png)
- [change_price.png](../../telegram-bot/src/main/resources/images/start/change_price.png)
- [dict.png](../../telegram-bot/src/main/resources/images/start/dict.png)
- [follow.png](../../telegram-bot/src/main/resources/images/start/follow.png)
- [history_of_purchase.png](../../telegram-bot/src/main/resources/images/start/history_of_purchase.png)
- [history_of_sell.png](../../telegram-bot/src/main/resources/images/start/history_of_sell.png)
- [history_of_transactions.png](../../telegram-bot/src/main/resources/images/start/history_of_transactions.png)
- [main_settings.png](../../telegram-bot/src/main/resources/images/start/main_settings.png)
- [market_history.png](../../telegram-bot/src/main/resources/images/start/market_history.png)
- [params.png](../../telegram-bot/src/main/resources/images/start/params.png)
- [portfolio.png](../../telegram-bot/src/main/resources/images/start/portfolio.png)
- [pswitch.png](../../telegram-bot/src/main/resources/images/start/pswitch.png)
- [ref.png](../../telegram-bot/src/main/resources/images/start/ref.png)
- [restrict.png](../../telegram-bot/src/main/resources/images/start/restrict.png)
- [scoring.png](../../telegram-bot/src/main/resources/images/start/scoring.png)
- [selling.png](../../telegram-bot/src/main/resources/images/start/selling.png)
- [start.png](../../telegram-bot/src/main/resources/images/start/start.png)
- [user.png](../../telegram-bot/src/main/resources/images/start/user.png)
- [waiting.png](../../telegram-bot/src/main/resources/images/start/waiting.png)
