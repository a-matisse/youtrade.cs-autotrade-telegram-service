# Overview API для лендинга

## Endpoint

```http
GET /api/web/v1/overview
```

Endpoint не попадает под внутренний interceptor `/api/telegram/**` и не требует `chatId` или API-ключ. Ответ кэшируется на backend и публично в HTTP на пять минут.

## Состав ответа

- `brand` — `Y.CS`.
- `telegramBotUrl` — ссылка CTA из `TELEGRAM_BOT_URL`.
- `pricing` — актуальные цены покупки, bargain-покупки, продажи и worker.
- `statistics` — агрегированная статистика без пользовательских данных.
- `generatedAt` — время формирования ответа.

`statistics.daily` всегда содержит 30 календарных точек, включая дни без продаж. Это позволяет строить график без дополнения дат на фронте.

Финансовые поля рассчитываются так:

```text
cost = Σ(boughtFor × (2 − purchaseMarket.topUpFee))
profitAfterWithdrawalFee = revenue × (1 − feeRate) − cost
```

`revenue` — фактическая сумма завершённых продаж, а `cost` — себестоимость проданных предметов с учётом комиссии пополнения площадки покупки. Все денежные значения выражены в USD и округлены до двух знаков.

## Пример

```json
{
  "brand": "Y.CS",
  "telegramBotUrl": "https://t.me/example_bot",
  "pricing": {
    "buySubPrices": { "CSFLOAT": 5.10 },
    "bargainBuySubPrices": { "CSFLOAT": 4.90 },
    "sellSubPrices": { "MARKET_CSGO": 1.75 },
    "workerPriceData": { "accCount": 1, "periodDays": 30, "price": 2.50 },
    "currency": 90.00
  },
  "statistics": {
    "purchases": 1200,
    "purchaseVolume": 85000.00,
    "sales": 900,
    "cost": 65716.10,
    "revenue": 72000.00,
    "daily": [
      {
        "date": "2026-09-18",
        "sales": 14,
        "cost": 930.12,
        "revenue": 1000.00
      }
    ]
  },
  "generatedAt": "2026-09-18T10:00:00Z"
}
```

## Старый endpoint цен

`GET /api/telegram/no-role/pay?chatId=...` возвращает цены, но находится под внутренним interceptor для `/api/telegram/**`. Для лендинга следует использовать `pricing` из overview.

## Лента курсов Steam

```http
GET /api/web/v1/steam/currency
```

Это отдельный открытый endpoint для верхнего ticker-а. Ответ отсортирован по `currency` и публично кэшируется пять минут.

```json
[
  {
    "currency": "RUB",
    "rate": 92.45,
    "time": 1789725600,
    "sourceItemname": "Example item",
    "sourcePriceTime": 1789725300,
    "source": "STEAM"
  }
]
```

Для основной ленты достаточно `currency` и `rate`. Остальные поля можно использовать во всплывающей подсказке с источником и актуальностью расчёта. Если endpoint временно вернул пустой массив, ленту следует скрыть без ошибки всей страницы.

## Крупнейшие сделки дня

```http
GET /api/web/v1/overview/deals
```

Открытый endpoint возвращает до трёх наиболее прибыльных завершённых сделок за текущий календарный день. Пользовательские, аккаунтные и токенные идентификаторы не возвращаются. Ответ кэшируется backend, браузером или CDN на пять минут.

```json
[
  {
    "itemName": "Example item",
    "imageUrl": "https://cdn.example/example-item.png",
    "soldAt": "2026-09-19T10:00:00",
    "source": "CSFLOAT",
    "destination": "MARKET_CSGO",
    "cost": 101.10,
    "revenue": 140.00,
    "profit": 38.90
  }
]
```

- `cost` — стоимость покупки с комиссией пополнения источника;
- `imageUrl` — ссылка на изображение предмета из внутреннего справочника; может быть `null`, если изображения нет;
- `revenue` — фактически полученная после продажи сумма до сценарной комиссии вывода;
- `profit` — прибыль до сценарной комиссии вывода: `revenue - cost`.

Порядок уже задан backend по убыванию `profit`. Если сделок за день меньше трёх, массив будет короче; если сделок нет — пустым.

## Документы Y.CS

```http
GET /api/web/v1/overview/documents
```

Открытый endpoint без `Authorization`, `X-Service-Key` и пользовательских параметров. Возвращает три текущих документа: условия обмена клиента, условия работы оператора и условия сервиса Y.CS. Поле `published` позволяет фронту отличать опубликованный документ от черновика. Получение документов не означает их принятие; принятие остаётся в авторизованном API пользователя.
