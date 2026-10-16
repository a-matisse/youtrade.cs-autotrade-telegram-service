# Фронт: комиссия Balance Market в открытом overview

`GET /api/web/v1/overview` без авторизации теперь содержит поле `balanceMarketPricing` рядом с существующим `pricing`:

```json
{
  "balanceMarketPricing": {
    "available": true,
    "totalFeePercent": 3.2,
    "networkFee": 1.5,
    "minAmount": 500,
    "maxAmount": 10000,
    "rateUpdatedAt": "2026-10-16T12:00:00"
  }
}
```

Показывайте `totalFeePercent` как **единую комиссию вывода**, а `networkFee` — отдельной фиксированной комиссией сети в USD. Ставка может измениться после обновления курса RedBalance: не закрепляйте `3.2` на фронте и не складывайте комиссии самостоятельно. `minAmount` и `maxAmount` — допустимая сумма Market.CS к списанию в USD.

При `available=false` остальные поля `balanceMarketPricing` равны `null`: скройте числовую ставку и сообщите, что расчёт временно недоступен. Для конкретной суммы и подтверждения заявки по-прежнему используйте авторизованный `POST /api/web/v1/balance-market/quote` или соответствующий Telegram API; итоговую выплату берите из его `estimatedPayout`. Ответ `overview` имеет `Cache-Control: no-store`.
