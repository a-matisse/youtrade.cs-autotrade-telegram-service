# API финансового кабинета Y.CS

Все маршруты в этом обновлении новые. Действующие маршруты профиля, входа и Telegram продолжают работать по прежнему контракту.

## Вход и выбор портфеля

Передавайте `Authorization: Bearer <access-token>` после входа через Telegram. В том же заголовке можно передать API-ключ вместо JWT. Владелец определяется по токену. Частные ответы содержат `Cache-Control: no-store`.

Без `tdpId` кабинет показывает все собственные портфели. Для выбранного портфеля передавайте его `portfolios[].tdpId` в запросах сводки, списка позиций, доходности и истории. Сервисный кошелёк всегда относится ко всему профилю.

Дата и время представлены целыми секундами UTC. Денежные поля с суффиксом `Usd` выражены в долларах. Процент `6.32` означает 6,32%. `null` означает, что значение недоступно. Суммы округляйте для отображения.

| Метод и путь | Параметры | Для чего использовать |
| --- | --- | --- |
| GET `/api/web/v1/user/portfolio` | `tdpId?` | Капитал, разбивка по портфелям, остатки площадок, активные торги, ожидаемый результат |
| GET `/api/web/v1/user/portfolio/positions` | `tdpId?`, `status?`, `afterId=0`, `limit=50` (1–100) | Отслеживаемые открытые позиции, стоимость и статус |
| GET `/api/web/v1/user/performance` | `tdpId?` и `period` или две границы `from_seconds`, `until_seconds` | Фактический результат закрытых сделок |
| GET `/api/web/v1/user/portfolio/history` | `tdpId?`, обязательные `from_seconds`, `until_seconds` | График оценочного капитала |
| GET `/api/web/v1/user/service-wallet` | — | Общий сервисный баланс для услуг `YOUTRADE_PRO` |

## Сводка

Верхний объект содержит `revision`, `calculatedAt`, `valuationBasis`, `capital` и `portfolios[]`. С указанным `tdpId` объект `capital` относится к тому же портфелю. Его `sharePercent` по-прежнему означает долю в общем капитале профиля.

| Поле | Значение |
| --- | --- |
| `capital.totalCapitalUsd` | Капитал по сохранённому расчёту с учётом модельных комиссий |
| `capital.buyAvailableUsd`, `buyPendingUsd`, `saleCashEstimatedUsd`, `inventoryEstimatedUsd` | Разложение той же оценки. Суммы по токенам отдельно доступны в `marketBalances` |
| `capital.oldestCapitalCalculatedAt`, `partial` | Время самого старого источника сводки и признак неполного или устаревшего расчёта |
| `capital.positionCount`, `valuedPositionCount` | Сколько позиций отслеживается и сколько вошло в расчёт ожидаемого результата |
| `capital.matchedOpenCostUsd`, `estimatedNetProceedsUsd`, `estimatedPnlUsd`, `estimatedReturnPercent` | Себестоимость, ожидаемые поступления, разница и процент к себестоимости для одного набора оценённых позиций |
| `portfolios[]` | `tdpId`, `name`, `source`, `destination`, `capital`, `sharePercent`, `marketBalances`, `activeBargainCount`, `offeredAmountUsd` |
| `marketBalances[]` | `market`, `side` (`BUY`/`SELL`), внутренний `accountId`, `availableUsd`, `pendingUsd`, `observedAt`, `observedCurrency` |

Время `calculatedAt` относится к сборке сводки. Время фактического расчёта капитала находится в `oldestCapitalCalculatedAt`. До первого наблюдения валюта и время денежного остатка могут отсутствовать. Показывайте сумму активных предложений отдельно от капитала.

### Открытые позиции

Ответ содержит `positions[]` и `nextAfterId`. Для следующей страницы передайте `nextAfterId` как `afterId` с теми же фильтрами. Когда курсор равен `null`, страниц больше нет. Фильтр `status` принимает значения `NOT_LISTED`, `TO_BE_LISTED`, `LISTED`, `DELISTED`. Позиции, ожидающие удаления, исключены.

У позиции есть `positionId`, `tdpId`, `itemName`, `boughtOn`, `destination`, `purchasePriceUsd`, `purchaseCostUsd`, `purchasedAt`, `unlockAt`, `held`, `listingStatus`, `strategyMinPriceUsd`, `strategyMaxPriceUsd`, `listingPriceUsd`, `marketEstimateUsd`, `estimateCollectedAt`, `estimatedNetProceedsUsd`, `estimatedPnlUsd` и `estimatedReturnPercent`. В первой версии `listingPriceUsd` равна `null`. `unlockAt` учитывает удержание Steam. `held` и `listingStatus` показываются независимо.

### Доход закрытых сделок

`period` принимает `HOURS_24`, `DAYS_7` или `DAYS_30`. Без периода и границ используется `DAYS_30`. Явные границы задают интервал `[from_seconds, until_seconds)`.

```json
{
  "fromSeconds": 1792742400,
  "untilSeconds": 1795334400,
  "calculatedAt": 1795334400,
  "tdpId": null,
  "feeBasis": "YCS_COMPLETED_SALE_V1",
  "incomeUsd": 7.20,
  "incomePercent": 6.315789,
  "saleVolumeUsd": 114.00,
  "matchedCostUsd": 101.10,
  "netProceedsUsd": 108.30,
  "tradeCount": 1,
  "unmatchedCount": 0
}
```

Подпись для `incomePercent` — «% от оборота продаж». Общий оборот расчёта указан в `saleVolumeUsd`. `unmatchedCount` показывает записи, для которых нет пригодной пары цены покупки и продажи. Если оборота нет, процент равен `null`.

### График капитала

Ответ содержит границы `fromSeconds`, `untilSeconds`, выбранный `tdpId`, начало пятиминутного ряда `fiveMinuteFrom` и массив `points[]`. У точки есть `snapshotAt`, `observedAt`, `revision`, `resolution`, `valuationBasis` и объект `capital` того же формата, что в сводке.

- `FIVE_MINUTES` — пятиминутные точки за 30 дней.
- `HOURLY` — часовые точки за остальную часть доступной истории, до пяти лет.
- `DAILY` — последняя доступная часовая точка UTC-дня в часовой части запроса на срок больше 90 дней.

Точки приходят в хронологическом порядке. Оставляйте пропуски там, где нет наблюдения. `observedAt` — фактическое время готовой сводки. Запись начинается после включения истории. График представляет стоимость капитала, а доход за период берите из `/performance`.

### Сервисный кошелёк

```json
{
  "product": "YOUTRADE_PRO",
  "serviceBalanceUsd": 42.15
}
```

Этот баланс предназначен для оплаты сервиса. Если записи кошелька нет, возвращается `null`.

## Ошибки и синхронизация

| Статус | Причина и поведение клиента |
| --- | --- |
| 400 | Неверный фильтр, лимит или диапазон. Исправить запрос |
| 401 | Нет действующего токена. Использовать текущий механизм refresh или входа |
| 403 | Профиль заблокирован |
| 404 | У авторизованного профиля нет указанного TDP |
| 503 | История отключена или временно недоступна, либо сводка обновляется. Остальные карточки можно показать независимо |

Синхронизируйте клиенты после выкладки новой версии `youtrade.cs-lis-tm`. Проверьте, что nginx проксирует `/api/web/v1/user/portfolio` с дочерними маршрутами, а также `/api/web/v1/user/performance` и `/api/web/v1/user/service-wallet`. Если уже проксируется префикс `/api/web/v1/user/`, маршруты покрыты. История заработает после [настройки ClickHouse](../portfolio-deployment.md).
