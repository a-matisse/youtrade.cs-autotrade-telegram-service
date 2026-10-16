# Окружения публичного лендинга

Frontend использует `VITE_API_BASE_URL` как origin backend. API-пути уже начинаются с `/api`, поэтому добавлять `/api` в значение переменной не нужно.

## Production

```env
VITE_API_BASE_URL=https://youtradecs.xyz
VITE_TELEGRAM_BOT_USERNAME=youtradecs_bot
VITE_MARKET_FEE_PERCENT=5
```

Комиссия вывода через Y.CS берётся из открытого `GET /api/web/v1/overview`
(`balanceMarketPricing.totalFeePercent`). При недоступном расчёте числовая ставка
скрывается. `VITE_MARKET_FEE_PERCENT` задаёт комиссию вывода Market.CS.
Ставки используются в карточках крупнейших сделок, легенде и графике результатов;
фиксированная комиссия сети показывается отдельно и не входит в расчёт графика.
Публичные CTA лендинга ведут на `https://t.me/youtradecs_bot`. Ссылка
`https://t.me/youtradecs_sup` используется только для явно подписанных
действий поддержки и консультации через посредника.

Примеры итоговых запросов:

- `https://youtradecs.xyz/api/web/v1/overview`
- `https://youtradecs.xyz/api/web/v1/overview/deals`
- `https://youtradecs.xyz/api/web/v1/steam/currency`

Это значение также является встроенным безопасным fallback, если переменная окружения при production-сборке не задана.

## Локальная разработка

Локальный backend подключается через development proxy Vite:

```env
VITE_DEV_PROXY_TARGET=http://localhost:21488
```

Значение хранится в `webapp/.env.development.local`. Файл исключён из Git и загружается только в development-режиме. Браузер запрашивает `/api` на origin Vite, а dev-сервер пересылает запрос backend. Это обходит отсутствие CORS-заголовков на локальном backend. После изменения env-файлов Vite необходимо перезапустить.
