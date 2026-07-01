# Задача для Codex: пересоздать MVP React webapp в `/webapp`

Ты работаешь в существующем проекте.

Работай автономно. Не задавай уточняющие вопросы, если можешь принять разумное решение на основе этого файла и существующего кода.

Все отчёты, README и TODO-файлы пиши на русском языке.

## Важное уточнение по проекту

В этом репозитории НЕТ backend-проекта.

В проекте есть только:

* существующий Telegram-клиент, далее `telegram-app`;
* новый web-клиент, который нужно создать в `/webapp`.

Фактические пути существующего Telegram-клиента в этом проекте находятся внутри:

`telegram-bot/src/main/java/cs/youtrade/autotrade/client/...`

Backend существует отдельно, в облаке / на другом сервере. Его код недоступен Codex для просмотра, изменения или запуска.

Поэтому:

* НЕ ищи backend-код в текущем проекте;
* НЕ создавай backend-код;
* НЕ добавляй backend controllers/services/entities;
* НЕ меняй backend-логику;
* НЕ создавай mock backend;
* НЕ прокидывай webapp-запросы через telegram-app;
* НЕ придумывай бизнес-логику во frontend.

Все знания об API и пользовательской логике нужно получить только из существующего telegram-app.

## Главная цель

Создать чистый MVP frontend-приложения на:

* React;
* Vite;
* TypeScript.

Приложение должно находиться в:

`/webapp`

`webapp` — это web/desktop-клиент для работы за компьютером.

`telegram-app` — это Telegram-клиент для работы, когда пользователь не за компьютером, плюс уведомления.

Оба клиента должны работать с одним внешним backend API.

## Важное правило по прошлой итерации

Если `/webapp` уже был создан прошлой итерацией Codex, пересоздай его начисто.

Перед началом:

1. Проверь, существует ли `/webapp`.
2. Если существует — удали/замени его содержимое полностью.
3. Создай `/webapp` заново по этой задаче.
4. Не пытайся аккуратно дорабатывать старую неудачную структуру.
5. Не сохраняй старые fake endpoints, fake API clients или временные заглушки из прошлой попытки.

Важно:

* можно полностью заменить файлы внутри `/webapp`;
* нельзя удалять или ломать `telegram-bot`;
* нельзя менять другие части проекта без необходимости;
* если вне `/webapp` есть изменения от прошлой попытки, не удаляй их вслепую, а укажи это в финальном отчёте.

## Главные источники контекста

Перед созданием webapp обязательно проанализируй существующие API-контракты telegram-app.

### Endpoints

Существующие backend endpoints, которые использует telegram-app, находятся здесь:

`telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoints`

Это главный источник информации о backend API.

Перед созданием `src/api` в webapp обязательно проанализируй эту директорию.

### DTO и API-контекст

DTO, request/response модели, API client logic и вспомогательные классы могут находиться здесь:

`telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade`

Используй эти классы как основу для TypeScript types в webapp.

### Пользовательская логика

Основная пользовательская логика telegram-app представлена в виде Telegram state machine здесь:

`telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu`

Используй эту state machine как главный источник пользовательских сценариев:

* какие экраны/состояния есть;
* какие действия доступны пользователю;
* какие переходы между состояниями есть;
* какие backend endpoints вызываются;
* какие данные вводятся;
* какие ошибки и результаты показываются.

Нужно перенести смысл сценариев в web UI, а не копировать Telegram-реализацию.

## Что запрещено копировать из telegram-app

Не переносить в webapp:

* `chatId`-авторизацию;
* Telegram `Update` handlers;
* `InlineKeyboard`;
* `callback_data`;
* `SendMessage`;
* Telegram state machine classes напрямую;
* любые классы, завязанные на Telegram API.

Telegram-app используется только как источник сценариев, DTO и API-контрактов.

## Backend endpoints

Не придумывай новые endpoint paths, пока не проверишь:

`telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoints`

Если endpoint уже существует в telegram-app-клиенте — используй его контракт.

Если DTO уже существует в:

`telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade`

используй его как основу для TypeScript-типа.

Если для webapp нужен endpoint, которого нет в telegram-app-контрактах, не реализуй fake business logic.

Вместо этого запиши недостающий endpoint на русском языке в:

`/webapp/TODO_BACKEND_ENDPOINTS.md`

Codex должен воспринимать этот файл как список задач для отдельного backend-проекта, который сейчас недоступен.

## Авторизация webapp

Авторизация webapp отличается от авторизации telegram-app.

Webapp должен использовать:

`Telegram Login Widget → backend validation → JWT`

НЕ использовать Telegram Mini App.

НЕ использовать `chatId` как identity для webapp.

Auth flow:

1. Пользователь открывает webapp.
2. Public pages доступны без авторизации.
3. Для входа пользователь использует Telegram Login Widget.
4. Frontend получает Telegram auth data от виджета.
5. Frontend отправляет Telegram auth data на внешний backend.
6. Backend проверяет Telegram signature/hash.
7. Backend создаёт или находит internal user.
8. Backend возвращает JWT.
9. Webapp сохраняет JWT.
10. Все protected API-запросы идут с заголовком:

`Authorization: Bearer <JWT>`

Правила:

* пользователь webapp определяется только по JWT, выпущенному backend;
* frontend не должен сам передавать trusted `userId`, `chatId`, `role`, `permissions`, `balance`, ownership или права доступа;
* внешний backend является единственным источником правды для identity, permissions, money/trading state и ownership checks;
* bot token, Telegram validation secret и любые секреты не должны попадать во frontend;
* если JWT отсутствует, истёк или backend вернул `401 Unauthorized` на protected request — redirect на `/auth`.

Если endpoint авторизации через Telegram Login Widget отсутствует в telegram-app-контрактах, добавь его в `/webapp/TODO_BACKEND_ENDPOINTS.md`.

## Public и protected pages

Public pages доступны без JWT:

* `/` — landing page;
* `/auth` — страница авторизации.

Protected pages требуют валидный JWT:

* `/dashboard`;
* остальные страницы приложения, созданные на основе сценариев telegram-app.

Landing page НЕ должен автоматически перекидывать на авторизацию.

На landing page должен быть CTA для входа через Telegram.

На первом этапе landing page должна быть простой, центральной, без сложного дизайна.

Страница `/auth` должна быть простой, с окном авторизации через Telegram Login Widget по центру.

Позже дизайн, тексты, позиционирование, динамические SVG и визуальный стиль будут уточняться отдельной задачей.

## Обязательное подключение Telegram-бота

После успешной авторизации через Telegram Login Widget и получения JWT webapp должен проверить, подключён ли Telegram-бот к пользователю.

Логика:

1. Пользователь авторизуется через Telegram Login Widget.
2. Backend выдаёт JWT.
3. Webapp вызывает `/me` или отдельный endpoint статуса.
4. Если `botLinked=false`, пользователь должен попасть на обязательный экран `/connect-bot`.
5. На `/connect-bot` нужно показать кнопку подключения Telegram-бота через deep link:

`https://t.me/<bot_username>?start=<link_token>`

6. Пользователь открывает бота и нажимает `/start <link_token>`.
7. Внешний backend связывает `chatId` с internal user.
8. Webapp проверяет статус подключения.
9. Когда `botLinked=true`, пользователь получает доступ к полноценному protected-приложению.

Важно:

* JWT можно выдать сразу после Telegram Login;
* но полный доступ к protected app должен быть только если:

  * JWT валиден;
  * `botLinked=true`;
* если JWT валиден, но `botLinked=false`, redirect на `/connect-bot`;
* polling статуса подключения можно делать каждые 5 секунд, но только на странице `/connect-bot`;
* не делать polling глобально по всему приложению.

Если endpoints для bot linking отсутствуют в telegram-app-контрактах, запиши их в `/webapp/TODO_BACKEND_ENDPOINTS.md`.

## Routing

Реализуй маршруты:

* `/` — public landing;
* `/auth` — public auth page;
* `/connect-bot` — protected onboarding для подключения Telegram-бота;
* `/dashboard` — protected dashboard;
* дополнительные protected pages по сценариям из telegram-app, если их можно реализовать на основе существующих endpoints.

Правила redirect:

* нет JWT → `/auth`;
* JWT невалиден или `401 Unauthorized` → очистить JWT и отправить на `/auth`;
* JWT валиден, но `botLinked=false` → `/connect-bot`;
* JWT валиден и `botLinked=true` → доступ к protected app.

## UI/UX

Сейчас нужен простой MVP-дизайн.

Вдохновение: Fragment.

Требования к MVP:

* чистый современный интерфейс;
* landing page;
* auth page;
* connect bot page;
* dashboard layout;
* карточки;
* аккуратные кнопки;
* loading states;
* error states;
* empty states;
* unauthorized states.

Добавь global loading screen при старте приложения, пока определяется auth state.

Loading screen должен быть простым, но архитектурно готовым к замене на красивую современную анимированную заставку.

Не делай сложный дизайн сейчас, но заложи структуру, чтобы потом можно было добавить:

* dynamic SVG на landing page;
* animated hero section;
* motion/animations;
* графики;
* статистику;
* dashboard widgets;
* trading-terminal style UI;
* современный SaaS-like интерфейс.

Не хардкодь UI так, чтобы потом было сложно заменить дизайн.

## Требуемая структура `/webapp`

Создай структуру:

`src/api` — API client и методы запросов к backend
`src/auth` — Telegram Login Widget и JWT auth logic
`src/routes` — public/protected routes
`src/pages` — страницы
`src/components/ui` — базовые UI-компоненты
`src/components/layout` — layout-компоненты
`src/hooks` — React hooks
`src/features` — feature-модули по сценариям из telegram-app
`src/assets` — ассеты, будущие SVG и визуальные элементы

## Что нужно реализовать

1. Проверить текущее состояние проекта.
2. Если `/webapp` существует — пересоздать его начисто.
3. Проанализировать endpoints telegram-app:

`telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade/endpoints`

4. Проанализировать DTO/API-контекст:

`telegram-bot/src/main/java/cs/youtrade/autotrade/client/util/autotrade`

5. Проанализировать state machine:

`telegram-bot/src/main/java/cs/youtrade/autotrade/client/telegram/menu`

6. Составить краткую карту пользовательских сценариев.
7. Создать React + Vite + TypeScript приложение в `/webapp`.
8. Реализовать public/protected routing.
9. Реализовать Telegram Login Widget integration.
10. Реализовать JWT storage.
11. Реализовать API client.
12. Автоматически добавлять `Authorization: Bearer <JWT>` в protected API-запросы.
13. Делать redirect на `/auth` при `401 Unauthorized`.
14. Реализовать `/connect-bot`.
15. Делать redirect на `/connect-bot`, если JWT валиден, но `botLinked=false`.
16. Реализовать polling статуса подключения бота только на `/connect-bot`.
17. Реализовать MVP landing page.
18. Реализовать auth page.
19. Реализовать connect bot page.
20. Реализовать dashboard.
21. Реализовать как можно больше основных сценариев из telegram-app через существующие backend API-контракты.
22. Если backend endpoint отсутствует — не имитировать бизнес-логику на frontend, а записать это в TODO-файл.

## Скрипты запуска

Добавь в `/webapp/package.json` скрипты:

* `npm run dev`
* `npm run build`
* `npm run local`
* `npm run lint`, если добавляешь linting

`npm run local` должен быть удобным alias для локального тест-запуска.

## Документация

Создай:

* `/webapp/README.md`
* `/webapp/.env.example`
* `/webapp/TODO_BACKEND_ENDPOINTS.md`

README должен быть на русском языке.

README должен описывать:

* как установить зависимости;
* как запустить webapp локально;
* какие env-переменные нужны;
* как работает Telegram Login Widget → JWT flow;
* как работает connect bot flow;
* какие backend endpoints ожидаются;
* какие endpoints восстановлены из telegram-app;
* какие endpoints отсутствуют и описаны в TODO.

## Env-переменные

В `.env.example` добавь:

`VITE_API_BASE_URL=`
`VITE_TELEGRAM_BOT_USERNAME=`
`VITE_TELEGRAM_LOGIN_BOT_NAME=`

Не создавай реальный `.env` с секретами.

Убедись, что `.env` игнорируется git.

## TODO для backend endpoints

Если для полноценной реализации webapp не хватает backend endpoint, не придумывай fake frontend logic.

Всё, чего не хватает, записывай на русском языке в:

`/webapp/TODO_BACKEND_ENDPOINTS.md`

Для каждого отсутствующего endpoint укажи:

* метод;
* путь;
* назначение;
* требуется ли JWT;
* request body;
* response body;
* ожидаемые ошибки;
* какая frontend-страница его использует;
* блокирует ли это MVP;
* комментарий, что нужно добавить на внешнем backend.

Формат TODO-файла должен быть понятным и пригодным для дальнейшей реализации backend-задач в отдельном backend-проекте.

Ожидаемые backend endpoints для webapp-архитектуры могут включать:

* `POST /auth/telegram` — проверить Telegram Login auth data и вернуть JWT;
* `GET /me` — вернуть текущего пользователя и `botLinked`;
* `POST /me/telegram-bot/link-token` — создать одноразовый token для deep link в Telegram-бота;
* `GET /me/telegram-bot/status` — проверить, подключён ли бот.

Если реальные endpoints в telegram-app называются иначе — используй существующие.

Если endpoint отсутствует — записывай в TODO.

## Ограничения безопасности

Не добавлять во frontend:

* bot token;
* backend secrets;
* private keys;
* API secrets;
* database credentials;
* admin tokens;
* payment/trading secrets;
* JWT signing secret.

Frontend должен считаться недоверенной средой.

Все проверки прав, денег, торговых операций, владения объектами и доступа должны оставаться на внешнем backend.

## Порядок работы

1. Сначала проверь текущее состояние проекта.
2. Если `/webapp` существует — пересоздай его начисто.
3. Проанализируй telegram-app endpoints, DTO и state machine.
4. Создай `/webapp`.
5. Реализуй MVP.
6. Запусти доступные install/build/lint checks.
7. Если какая-то команда не запускается — явно напиши причину.

В конце отчёта напиши на русском:

* была ли удалена/пересоздана старая `/webapp`;
* какие файлы созданы;
* какие файлы изменены;
* какие страницы реализованы;
* какие сценарии реализованы;
* какие endpoints были найдены в telegram-app;
* какие backend endpoints отсутствуют;
* что записано в `/webapp/TODO_BACKEND_ENDPOINTS.md`;
* как запустить webapp локально;
* что осталось TODO.

Главное: создать чистый рабочий MVP frontend в `/webapp`, используя telegram-app как единственный источник знаний об API и пользовательских сценариях, не трогая внешний backend, потому что его код недоступен в этом проекте.
