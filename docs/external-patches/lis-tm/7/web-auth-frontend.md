# Авторизация Telegram OIDC для frontend Y.CS

## Адреса

Production origin — `https://youtradecs.xyz`. Auth API — `/api/web/v1/auth`.
Backend callback — `/api/web/v1/auth/telegram/callback`, frontend callback —
`/auth/callback`. Frontend должен обслуживать этот маршрут как страницу приложения.
Текущую форму на старом Telegram Login Widget нужно подключить к этому OIDC flow.

В этой конфигурации frontend и API работают на одном HTTPS-origin. Вход
начинается на том же хосте, на котором принимается backend callback. Другой
домен потребует согласования redirect URI, Origin, cookie и CORS. Callback
задаётся серверной конфигурацией и не принимается из пользовательского запроса.

## Вход

Первый успешный вход автоматически создаёт аккаунт Y.CS со стандартными
параметрами, кошельком и настройками. Отдельный запрос регистрации и предварительный
запуск бота не нужны. Повторный вход сохраняет существующий аккаунт и его данные.
Вход через Telegram сам по себе не подтверждает возможность отправлять пользователю
сообщения ботом; `botLinked` сейчас означает наличие Telegram ID, а не разрешение на сообщения.

1. Кнопка «Войти через Telegram» открывает навигацией
   `/api/web/v1/auth/telegram`, например через `window.location.assign(...)`.
2. Backend ставит временную HttpOnly Secure cookie и перенаправляет в Telegram.
3. После подтверждения Telegram возвращает браузер на backend callback.
   Backend проверяет state, PKCE, подпись и claims ID token, создаёт сессию,
   ставит refresh-cookie и перенаправляет на `/auth/callback`.
4. Страница `/auth/callback` вызывает `POST /api/web/v1/auth/refresh`.
5. Frontend сохраняет полученный access-токен в памяти приложения, вызывает
   `GET /api/web/v1/auth/me` и открывает `/dashboard`.

Вход начинается навигацией браузера, backend callback обслуживает backend.
Токены в URL не передаются. Frontend получает access-токен из ответа refresh.

## HTTP-контракт

| Метод | Путь | Результат |
| --- | --- | --- |
| GET | `/api/web/v1/auth/telegram` | HTTP 302 в Telegram, cookie заявки входа |
| GET | `/api/web/v1/auth/telegram/callback?code=…&state=…` | HTTP 302 на frontend callback, refresh-cookie |
| POST | `/api/web/v1/auth/refresh` | Новая пара access/refresh, access в JSON, refresh в cookie |
| GET | `/api/web/v1/auth/me` | Текущий пользователь по JWT или API-ключу |
| POST | `/api/web/v1/auth/logout` | HTTP 204, отзыв текущей сессии, очистка refresh-cookie |

### Refresh

```http
POST /api/web/v1/auth/refresh
Origin: https://youtradecs.xyz
Cookie: __Secure-ycs-refresh=…
```

Тело запроса не требуется. Браузер отправляет Origin и cookie автоматически.

```typescript
const response = await fetch('/api/web/v1/auth/refresh', {
  method: 'POST',
  credentials: 'same-origin',
  headers: {Accept: 'application/json'},
});
```

Успешный ответ:

```json
{"accessToken":"ey…","tokenType":"Bearer","expiresIn":3600}
```

`expiresIn` — оставшийся срок access-токена в секундах. Обновлённый refresh
приходит через Set-Cookie. Cookie `__Secure-ycs-refresh` имеет HttpOnly, Secure,
SameSite=Strict и Path=/api/web/v1/auth. JavaScript не читает её значение.
Access-токен хранится в памяти приложения. При перезагрузке страницы приложение
восстанавливает его через refresh-cookie.

### Пользователь и авторизованные запросы

```http
GET /api/web/v1/auth/me
Authorization: Bearer <accessToken>
```

```json
{"id":42,"telegramId":777,"botLinked":true}
```

Остальные пользовательские web-запросы передают такой же Authorization.
Например, `/api/web/v1/user/general/info`, `/api/web/v1/user/accounts/v2`.
Существующие API-клиенты также могут передавать API-ключ в Bearer-заголовке.
API-ключ не участвует в обновлении JWT-сессии.

Внутренние `/api/telegram/...` требуют сервисного ключа. Кабинет должен
использовать соответствующие web-эндпоинты и проверку владельца на backend.

### Выход

```http
POST /api/web/v1/auth/logout
Authorization: Bearer <accessToken>
```

Успех — HTTP 204. Backend отзывает текущую сессию и очищает refresh-cookie.
Frontend очищает access-токен и данные пользователя. Другие устройства
продолжают работать в своих сессиях. Для выхода нужен действующий access-токен.
Если он истёк, сначала выполнить один refresh, затем logout. При HTTP 401
от refresh локально очистить авторизацию.

## Параллельные запросы и вкладки

На один браузер должен выполняться только один refresh одновременно.
Все ожидающие запросы должны разделять одну Promise обновления. Обновление
также нужно координировать между вкладками, например через Web Locks и
BroadcastChannel. Cookie refresh общая для вкладок одного браузера.

После refresh меняются оба токена. Предыдущий access перестаёт действовать.
Повторное использование предыдущего корректно подписанного refresh отзывает
всю текущую сессию браузера. Поэтому refresh нельзя автоматически повторять
после сетевого сбоя с неизвестным результатом — потребуется новый вход.
Новый вход с другого устройства создаёт независимую сессию.

По expiresIn можно обновлять access перед истечением. При HTTP 401 обычного
запроса можно выполнить единственный refresh и повторить исходный запрос.
Сам refresh нужно исключить из обработчика автоматического обновления,
чтобы не возник рекурсивный цикл. Изменяющие запросы повторяются только
когда известен результат предыдущей попытки или предусмотрена идемпотентность.

## Коды ответа

| HTTP | Действие frontend |
| --- | --- |
| 200 / 204 | Использовать ответ, обновить локальное состояние |
| 302 | Навигация в Telegram или возврат на frontend callback |
| 401 | Credentials недействительны, истекли или отозваны; после неудачного refresh открыть вход |
| 403 | Пользователь заблокирован либо Origin не соответствует сайту |
| 502 | Ошибка связи с Telegram; начать вход заново |
| 503 | Вход ещё не настроен на backend |

На старте приложения HTTP 401 от refresh означает гостевой режим. Ошибка
backend callback может отображаться браузером как HTTP-ошибка. Повторный вход
начинается через `/api/web/v1/auth/telegram`, поскольку старый state уже использован.
Access/refresh-токены, OAuth code и секреты не должны попадать в логи, аналитику и URL.

## Сроки и хранение

По умолчанию access JWT действует один час, refresh-сессия — 30 дней от входа,
заявка входа — пять минут. Refresh обновляет credentials в пределах исходного
срока сессии. Каждый новый вход создаёт отдельную строку, связь с пользователем 1:N.

В PostgreSQL сохраняются идентификаторы, даты, User-Agent и хеш refresh-токена.
Backend проверяет принадлежность аккаунтов, предметов и параметров через
существующие сервисы.

## Перед подключением

Потребуются настройки BotFather, зарегистрированный callback, JWT secret,
миграция таблицы сессий и маршруты Nginx. Серверная инструкция находится в
[web-auth.md](web-auth.md). До production-включения проверить вход в браузере,
перезагрузку страницы, истечение access, параллельные запросы и вкладки,
выход и независимость сессий разных устройств.

Протокол описан в [документации Telegram](https://core.telegram.org/bots/telegram-login).
Требования к PKCE и защите refresh-токенов изложены в
[OAuth 2.0 Security BCP](https://www.rfc-editor.org/rfc/rfc9700.html).
