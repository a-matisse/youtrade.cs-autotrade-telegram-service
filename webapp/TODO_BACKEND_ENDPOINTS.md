# TODO: endpoints внешнего backend для webapp

В Telegram-клиенте web-авторизация и привязка бота отсутствуют. Все существующие бизнес-endpoints находятся под `/api/telegram/...`, требуют служебный `X-Service-Key` и передают `chatId`. Браузер не должен получать service key или утверждать identity через `chatId`. Ниже перечислены обязательные JWT-контракты для отдельного backend-проекта.

## 1. Авторизация через Telegram Login Widget

- **Метод и путь:** `POST /auth/telegram`
- **Назначение:** проверить Telegram hash, создать или найти внутреннего пользователя, выдать JWT.
- **JWT:** нет.
- **Request body:** `{ id, first_name, last_name?, username?, photo_url?, auth_date, hash }` — данные виджета без преобразования.
- **Response body:** `{ token: string }` (допустимы согласованные имена `jwt` или `accessToken`).
- **Ошибки:** `400` неполные данные, `401` неверная/устаревшая подпись, `429` лимит запросов, `5xx` внутренняя ошибка.
- **Страница:** `/auth`.
- **Блокирует MVP:** да, без endpoint невозможен реальный вход.
- **Backend-задача:** валидировать подпись только на сервере; не отдавать bot token или signing secret.

## 2. Текущий пользователь

- **Метод и путь:** `GET /me`
- **Назначение:** проверить JWT и вернуть состояние пользователя, включая связь с ботом.
- **JWT:** да.
- **Request body:** отсутствует.
- **Response body:** `{ id: string | number, displayName?: string, username?: string, botLinked: boolean }`.
- **Ошибки:** `401` отсутствующий/истёкший JWT, `403` заблокированный пользователь, `5xx`.
- **Страницы:** auth bootstrap, guards, `/connect-bot`, layout.
- **Блокирует MVP:** да.
- **Backend-задача:** вычислять identity и `botLinked` только по JWT.

## 3. Одноразовый token подключения бота

- **Метод и путь:** `POST /me/telegram-bot/link-token`
- **Назначение:** создать короткоживущий одноразовый token для `https://t.me/<bot>?start=<token>`.
- **JWT:** да.
- **Request body:** отсутствует.
- **Response body:** `{ linkToken: string, expiresAt?: string }` или `{ deepLink: string, expiresAt?: string }`.
- **Ошибки:** `401`, `409` бот уже связан, `429`, `5xx`.
- **Страница:** `/connect-bot`.
- **Блокирует MVP:** да, если backend не возвращает готовую deep link другим способом.
- **Backend-задача:** token должен быть одноразовым, ограниченным по времени и привязанным к внутреннему user id.

## 4. Статус подключения бота

- **Метод и путь:** `GET /me/telegram-bot/status`
- **Назначение:** проверить завершение связи после `/start <token>`.
- **JWT:** да.
- **Request body:** отсутствует.
- **Response body:** `{ botLinked: boolean }` с теми же базовыми полями, что у `/me`, либо полностью совместимый ответ `/me`.
- **Ошибки:** `401`, `429`, `5xx`.
- **Страница:** `/connect-bot`, polling раз в пять секунд.
- **Блокирует MVP:** да.
- **Backend-задача:** разрешить безопасный polling и связать Telegram `chatId` с user id только внутри backend.

## 5. JWT-совместимый доступ к бизнес-endpoints

- **Метод и путь:** существующие методы и пути `/api/telegram/user/**`, либо эквивалентные `/api/web/user/**`, явно согласованные с frontend.
- **Назначение:** аккаунты, портфель, параметры, словари, токены, переключатели автопокупки/автопродажи, scoring и рефералы.
- **JWT:** да.
- **Request body:** DTO должны сохранять бизнес-поля существующих Java DTO; `chatId`, `userId`, role и ownership передаваться не должны.
- **Response body:** сохранить полезные поля существующих DTO (`FcdGeneralAccInfoDto`, `FcdAccountsV2Dto`, `FcdParamsListDto`, inventory/history DTO и другие).
- **Ошибки:** `400` валидация, `401`, `403` права/владение, `404`, `409`, `422`, `429`, `5xx`.
- **Страницы:** `/dashboard`, `/accounts`, `/portfolio`, `/parameters`, `/dictionaries` и будущие формы операций.
- **Блокирует MVP:** частично; UI и auth работают, но реальные данные не загрузятся, если backend продолжает требовать `chatId` и `X-Service-Key`.
- **Backend-задача:** принимать Bearer JWT, определять владельца сервером и не требовать `X-Service-Key`/`chatId` от браузера. Желательно сохранить текущие response DTO и пути, чтобы не дублировать бизнес API.

## Оставшиеся задачи после базовых контрактов

- Зафиксировать единую envelope-схему ответов (`data`, `result` либо прямой DTO) и формат ошибок.
- Опубликовать OpenAPI-схему внешнего backend.
- Добавить JWT-совместимые операции изменения параметров, словарей, аккаунтов и торговых переключателей с серверной проверкой владения.
- Определить CORS для origin webapp и политику refresh token/срока жизни access token.
