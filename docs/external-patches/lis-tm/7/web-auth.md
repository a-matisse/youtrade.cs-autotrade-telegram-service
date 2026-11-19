# Telegram Login для web API

## Конфигурация

Заполнить в серверном `default.env`:

```dotenv
TELEGRAM_OIDC_CLIENT_ID=
TELEGRAM_OIDC_CLIENT_SECRET=
TELEGRAM_OIDC_REDIRECT_URI=https://youtradecs.xyz/api/web/v1/auth/telegram/callback
YOUTRADE_WEB_AUTH_REDIRECT_URI=https://youtradecs.xyz/auth/callback
YOUTRADE_WEB_JWT_SECRET=
```

Client ID и Client Secret выдаёт BotFather в настройках Login Widget. Там же
зарегистрировать точный redirect URI. JWT secret — отдельный случайный ключ,
минимум 32 байта, закодированный Base64. Например, `openssl rand -base64 32`.
Значения хранятся в окружении сервиса.

## Контракт

| Метод | Путь | Поведение |
| --- | --- | --- |
| GET | `/api/web/v1/auth/telegram` | Ставит HttpOnly Secure cookie заявки и перенаправляет браузер в Telegram |
| GET | `/api/web/v1/auth/telegram/callback?code=…&state=…` | Проверяет cookie/state, обменивает code с PKCE, проверяет ID token, ставит refresh-cookie и перенаправляет на frontend callback |
| GET | `/api/web/v1/auth/me` | Принимает `Authorization: Bearer <JWT или API-ключ>` и возвращает пользователя |
| POST | `/api/web/v1/auth/refresh` | Принимает refresh-cookie и Origin сайта, атомарно обновляет access/refresh |
| POST | `/api/web/v1/auth/logout` | Принимает наш JWT и отзывает текущую web-сессию в PostgreSQL |

`POST /refresh` возвращает:

```json
{"accessToken":"ey…","tokenType":"Bearer","expiresIn":3600}
```

`me` возвращает:

```json
{"id":42,"telegramId":777,"botLinked":true}
```

JWT действует один час. Refresh-сессия имеет абсолютный срок 30 дней от входа.
Refresh передаётся в HttpOnly Secure SameSite=Strict cookie `__Secure-ycs-refresh`,
доступной по пути `/api/web/v1/auth`. При запросе refresh браузер отправляет cookie
и `Origin: https://youtradecs.xyz`. При использовании fetch нужен `credentials: 'same-origin'`.
State, nonce и PKCE verifier живут пять минут в Redis.
Callback привязан к браузеру через cookie, поэтому начало входа и callback
должны выполняться на одном HTTPS-хосте. Повторное использование callback
завершается HTTP 401.

Web-сессии хранятся в PostgreSQL в `youtrade_web_session_data`, связь с
`telegram_data` — 1:N. Каждый вход создаёт отдельную сессию. Таблица хранит
идентификаторы сессии и access-токена, SHA-256 хеш refresh-токена, User-Agent,
время создания и обновления, сроки действия и дату отзыва. Сами токены в БД
не сохраняются. При refresh меняются оба токена; предыдущая пара становится
недействительной. Обновление и выход используют блокировку строки сессии.
Повторное предъявление старого подписанного refresh-токена отзывает сессию
данного браузера. Сроки access и refresh и их audience проверяются раздельно.
Для параллельных запросов одного браузера frontend должен объединять обновление
токенов в один выполняющийся запрос.

Backend находит существующий аккаунт Y.CS по проверенному Telegram `id`.
Если аккаунта ещё нет, backend создаёт его через стандартный
`TelegramDataService.createTelegramUser`: роль FULL, кошелёк, preferences,
параметры CSFloat → Market.CS и доступ к баргейнам по текущей конфигурации.
Предварительная регистрация через бота не требуется. Создание происходит только
после проверки ID token; повторный вход использует существующего пользователя.
Заблокированный аккаунт получает HTTP 403. Некорректный JWT возвращает HTTP 401. Незаполненные настройки входа
возвращают HTTP 503. Сбой запроса к Telegram возвращает HTTP 502.

Общие lookup-методы `TelegramDataService` и проверка пароля экспорта аккаунтов
поддерживают обе разновидности Bearer credentials. JWT определяет внутренний
ID пользователя, а API-ключ разрешается существующим JPA-репозиторием.
Проверки принадлежности предметов, аккаунтов и параметров остаются в сервисах.

Разделение платформенного пользователя и Telegram, а также пользовательский кабинет
описаны в [плане следующего обновления](user-account-next-update-plan.md).

## Подключение

В Nginx должны проксироваться `/api/web/v1/auth/` и нужные пользовательские
web-эндпоинты на `127.0.0.1:21488`. Шаблон находится в соседнем frontend-проекте.
Конфигурация окружения применяется пересозданием контейнера после сборки.
Перед выкладкой применить `flyway/V20261119_01__web_auth_sessions.sql` к базе
`lisTg`. Миграция создаёт таблицу сессий, FK и индексы владельца и срока действия.

Frontend пока использует старый iframe Telegram Login Widget и `/auth/telegram`.
Интеграция нового flow описана отдельно в [web-auth-frontend.md](web-auth-frontend.md).
После backend callback браузер перенаправляется на `/auth/callback`, где
frontend вызывает refresh, получает access JWT и загружает текущего пользователя.

Проверка реального входа, JWT/API-доступа, срока действия и выхода запланирована
после заполнения настроек. Локальная симуляция Telegram API подготовлена:

```bash
postman simulation run postman/simulations/telegram-oidc.sim.yaml --port 4920 --no-history
```

Она публикует тестовые JWKS и проверяет client credentials, PKCE и redirect URI.
Варианты ответа включают неверную подпись, issuer, audience, nonce и просроченный
токен. Запуск проверок:

```bash
mvn -Dtest=WebAuthControllerTest -Dtelegram.oidc.simulation.url=http://127.0.0.1:4920/telegram test
```

Симуляция проверяет реальный HTTP-клиент и обработку JWKS. Проверки сессий
используют подмены Redis и JPA-репозитория. Реальный браузерный вход и конкурентные
транзакции PostgreSQL требуют отдельной проверки после конфигурации окружения.
