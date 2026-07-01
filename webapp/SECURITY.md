# Безопасность MVP

## Реализовано во frontend

- JWT хранится в `sessionStorage`, очищается при `401`, выходе и закрытии вкладки. Старое значение из `localStorage` удаляется при очистке сессии.
- JWT проверяется на базовый формат и ограничивается по длине до сохранения.
- `Authorization` формируется API-клиентом последним и не может быть переопределён вызывающим кодом.
- API-запросы выполняются без cookies и referrer, с тайм-аутом 15 секунд.
- Telegram auth data проходит базовую структурную проверку. Проверка подписи и времени остаётся обязательной на backend.
- Deep link допускает только HTTPS, домен `t.me` и настроенное имя бота; link token ограничен безопасным алфавитом и длиной.
- Telegram Widget не запрашивает избыточное разрешение `request-access=write`.
- В HTML задана базовая Content Security Policy; React не использует `dangerouslySetInnerHTML`.
- Зависимости проверяются командой `npm audit`.

## Обязательно настроить при публикации

Статический хостинг или reverse proxy должен отдавать заголовки:

- `Strict-Transport-Security: max-age=31536000; includeSubDomains` после полного перехода домена на HTTPS;
- `Content-Security-Policy` с точным origin внешнего API в `connect-src` вместо общей схемы `https:`;
- `X-Content-Type-Options: nosniff`;
- `Referrer-Policy: no-referrer`;
- `Permissions-Policy: camera=(), microphone=(), geolocation=(), payment=()`;
- `Cross-Origin-Opener-Policy: same-origin-allow-popups` после проверки совместимости Telegram Login;
- запрет встраивания приложения через `frame-ancestors 'none'` в HTTP CSP-заголовке.

Backend обязан проверять Telegram hash, свежесть `auth_date`, JWT, права и владение объектами; применять строгий CORS allowlist и rate limiting; не принимать `chatId`, role или user id как доверенные данные браузера. Для долгоживущей сессии предпочтителен refresh token в cookie с флагами `HttpOnly`, `Secure`, `SameSite=Strict` или `Lax` по согласованному flow.
