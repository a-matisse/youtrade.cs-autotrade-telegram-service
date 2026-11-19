# Безопасность MVP

## Реализовано во frontend

- Access JWT хранится только в памяти. Старые значения `youtrade.jwt` удаляются из sessionStorage и localStorage.
- Refresh находится в серверной HttpOnly Secure SameSite=Strict cookie; JavaScript не читает её содержимое.
- `Authorization` формируется API-клиентом последним и не может быть переопределён вызывающим кодом.
- Auth и запросы с Authorization отправляются только на origin сайта. Запросы используют `credentials: same-origin`, `no-referrer` и тайм-аут 15 секунд.
- Проверка OIDC state, PKCE, подписи и claims выполняется backend. Access и refresh не передаются в URL.
- Web Locks и BroadcastChannel согласуют refresh между вкладками; одна Promise объединяет запросы в одной вкладке. Access передаётся только в памяти между вкладками того же origin.
- Refresh после сетевого сбоя с неизвестным результатом не повторяется. Изменяющие пользовательские запросы автоматически не переотправляются после обновления сессии.
- Выход отзывает серверную сессию и очищает состояние открытых вкладок. При ошибке выхода локальные credentials удаляются, а пользователь получает сообщение, что серверный выход не подтверждён.
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

Backend обязан проверять OIDC, JWT, Origin, права и владение объектами; применять rate limiting; не принимать `chatId`, role или user id как доверенные данные браузера. Параметры HTTPS, callback, cookie, JWT secret и миграции настраиваются по [патчу 7](../docs/external-patches/lis-tm/7/web-auth.md). Локальная имитация API не подтверждает правильность production-настроек Telegram.
