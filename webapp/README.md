# YouTrade Webapp

Чистый MVP web/desktop-клиента на React, Vite и TypeScript. Приложение обращается напрямую к внешнему backend и не использует Telegram-клиент как прокси.

## Локальный запуск

Требуется Node.js 20 или новее.

```bash
npm install
copy .env.example .env
npm run local
```

Для обычного dev-сервера доступна команда `npm run dev`. Проверки: `npm run lint` и `npm run build`.

## Переменные окружения

- `VITE_API_BASE_URL` — адрес внешнего backend без завершающего `/`.
- `VITE_TELEGRAM_BOT_USERNAME` — username Telegram-бота для deep link, без `@`.
- `VITE_TELEGRAM_LOGIN_BOT_NAME` — имя бота, зарегистрированное для Telegram Login Widget.

Секреты, bot token и JWT signing key во frontend не используются. Файл `.env` игнорируется Git.

## Авторизация и подключение бота

На `/auth` официальный Telegram Login Widget возвращает подписанные auth-данные. Frontend передаёт их в `POST /auth/telegram`, сохраняет полученный JWT в `sessionStorage` и вызывает `GET /me`. Все защищённые запросы получают `Authorization: Bearer <JWT>`. При ответе `401` токен удаляется, guards переводят пользователя на `/auth`. Токен очищается при закрытии вкладки; для долгоживущей сессии backend должен предоставить безопасную схему с `HttpOnly`, `Secure`, `SameSite` cookie.

Если `GET /me` сообщает `botLinked=false`, доступен только `/connect-bot`. Страница получает одноразовую ссылку через `POST /me/telegram-bot/link-token` и проверяет статус через `GET /me/telegram-bot/status` каждые пять секунд. Polling работает только на этой странице.

## Маршруты

- `/` и `/auth` — публичные.
- `/connect-bot` — защищённый обязательный onboarding.
- `/dashboard`, `/accounts`, `/portfolio`, `/parameters`, `/dictionaries` — требуют JWT и `botLinked=true`.

Сценарии MVP сделаны read-only намеренно: исходные Telegram-контракты требуют доверенный `chatId` и служебный `X-Service-Key`. Передавать их из браузера небезопасно. Операции изменения станут доступны после появления JWT-совместимых backend-контрактов.

## Контракты, восстановленные из Telegram-клиента

- `GET /api/telegram/user/general/info` — общая информация и баланс.
- `GET /api/telegram/user/accounts/v2` — аккаунты с пагинацией.
- `GET /api/telegram/user/params/all` — наборы параметров.
- `GET /api/telegram/user/v2/inventory` — инвентарь.
- `GET /api/telegram/user/v2/selling` — выставленные позиции.
- `GET /api/telegram/user/sell/history/buy`, `/history/sell`, `/waiting` — история и ожидание.
- `GET /api/telegram/user/buy/words/included`, `/excluded` — словари.

Также найдены операции управления токенами покупки/продажи, автопокупкой, автопродажей, scoring, параметрами, рефералами и аккаунтами. В текущем MVP они не вызываются, пока backend не обеспечит идентификацию владельца по JWT.

Недостающие web-контракты подробно описаны в [TODO_BACKEND_ENDPOINTS.md](./TODO_BACKEND_ENDPOINTS.md).
