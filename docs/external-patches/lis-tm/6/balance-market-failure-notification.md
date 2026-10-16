# Микропатч: неисполненная заявка Balance Market

Добавить в Telegram-получатель `notification-stream` обработчик `type=BALANCE_MARKET_FAILED`. Backend публикует данные в JSON-поле `payload` записи Redis Stream, как для `BALANCE_MARKET_PAYOUT`. Готового текста нет; бот формирует сообщение сам.

```json
{
  "product": "YOUTRADE_PRO",
  "type": "BALANCE_MARKET_FAILED",
  "tdId": 1,
  "chatId": 123,
  "sessionId": 19,
  "steamTokenId": 2,
  "sourceAmount": 500.000,
  "withdrawalMethod": "USDT_TRC20",
  "reason": "USER_TRANSFER_NOT_RECEIVED",
  "failedAt": "2026-10-17T10:15"
}
```

`reason` и `failedAt` могут быть `null`; `failedAt` — время UTC. Для `USER_TRANSFER_NOT_RECEIVED` можно предложить проверить баланс исходного аккаунта. `REFUNDED_TO_SOURCE_ACCOUNT` означает возврат на Market.CS; не показывать его как выплату USDT. Для остальных или неизвестных кодов предложить обратиться в поддержку. Код причины не следует показывать пользователю как сырой текст.

Дедуплицировать сообщение по паре (`type`, `sessionId`): событие может прийти повторно при сбое между публикацией и записью `notification_sent_at`. После получения события и после переподключения перечитывать заявку через Telegram API. Сайт получает состояние заявки через web API и не читает Redis Stream.
