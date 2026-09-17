# Общая оферта Y.CS: изменения для фронта

В существующий API версионированных документов добавлен тип `YCS_SERVICE_TERMS`. Он использует ту же модель чтения и принятия, что P2P-документы, но публикуется независимо от них.

## Получение документа

- Web: `GET /api/web/v1/user/p2p/documents`
- Telegram backend: `GET /api/telegram/user/p2p/documents?chatId=<chatId>`

В массиве нужно найти:

```json
{
  "type": "YCS_SERVICE_TERMS",
  "version": "1",
  "locale": "ru",
  "contentHash": "<sha256>",
  "content": "<markdown>",
  "published": false
}
```

Фронт показывает Markdown полностью. При `published=false` документ является черновиком: кнопку принятия показывать нельзя.

## Принятие

- Web: `POST /api/web/v1/user/p2p/document-acceptances`
- Telegram backend: `POST /api/telegram/user/p2p/document-acceptances?chatId=<chatId>`
- Заголовок: `Idempotency-Key: <uuid>`

```json
{
  "type": "YCS_SERVICE_TERMS",
  "version": "1",
  "contentHash": "<значение из GET>"
}
```

Не пересчитывать hash из HTML и не хранить согласие только локально. После успешного POST использовать серверный ответ `Acceptance`; историю читать через `GET /document-acceptances`.

Общая оферта всегда возвращается с `published=true` и доступна для ознакомления. Её принятие пока добровольно и не ограничивает функциональность. `P2P_DOCUMENTS_PUBLISHED` отдельно управляет клиентскими и операторскими P2P-условиями.
