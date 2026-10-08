# API защиты и экспорта данных аккаунтов

Защитный пароль общий для пользователя `TelegramData` и всех его наборов торговых
параметров. Хранится в `TelegramDataPreferences` как хеш PBKDF2-HMAC-SHA256 с
индивидуальной солью. Исходный пароль не сохраняется и в ответах API не выдаётся.

## Установить пароль

`POST /api/web/v1/user/properties/account-export-password`.
Доступ контролируется существующим `Authorization: Bearer <user-api-key>`.

```json
{"password":"new-secret-password"}
```

При успехе возвращается `204` без тела. Пароль должен содержать от 12 до 256
символов. Если пароль уже установлен, возвращается `409`: повторная установка
не может заменить его без знания текущего пароля.

## Сменить пароль

`POST /api/web/v1/user/properties/account-export-password/change`.
Используется тот же заголовок `Authorization`.

```json
{"currentPassword":"old-secret-password","newPassword":"new-secret-password"}
```

При успехе возвращается `204` без тела. Неверный текущий пароль получает `403`,
отсутствие установленного пароля — `409`. Пять неверных попыток блокируют
проверку на 15 минут; во время блокировки возвращается `429`. Административный
сброс снимает блокировку.

## Административно сбросить пароль

`POST /api/debug/users/{tdId}/account-export-password/reset`.
Здесь `tdId` — внутренний ID пользователя, не Telegram chat ID. Доступ
контролируется существующим заголовком `X-Debug-Key`. Старый пароль не требуется.

```json
{"password":"replacement-secret-password"}
```

При успехе возвращается `204` без тела, при отсутствии пользователя — `404`.
Ограничение длины нового пароля такое же: от 12 до 256 символов.

Для всех трёх ручек некорректный новый пароль получает `400`; пользовательские
ручки требуют действующего bearer-ключа. Успешные ответы содержат
`Cache-Control: no-store`. Тела запросов с паролями нельзя записывать в логи.

## Внутренний контракт MarketApp

В [патче MarketApp](../external-patches/marketapp-service/export-credentials.md)
описана ручка `POST /api/token/export-credentials` с `X-Service-Key`. Она
принимает `tdId` и список `ystIds`, проверяет принадлежность всех Steam-токенов
пользователю и возвращает для каждого Steam-логин, пароль и текущий Guard-код.
Ответ сохраняет порядок `ystIds`; ошибки ASF отражаются в соответствующей
записи. Поля и коды ошибок определены в документе патча. Эта ручка является
контрактом для отдельного `youtrade.cs-marketapp-service`; её код не входит в
текущие изменения данного backend.

## Выгрузить данные одного аккаунта

`POST /api/web/v1/user/accounts/v2/export-credentials`.
Нужны `Authorization: Bearer <user-api-key>`, ID выбранного Steam-аккаунта
и защитный пароль в JSON-теле.

```json
{"steamTokenId":1703,"password":"account-export-password"}
```

После проверки пароля backend ищет только указанный аккаунт и проверяет его
принадлежность пользователю среди всех наборов параметров. Для аккаунта с
подключённым worker он отправляет в MarketApp `ystIds` с единственным
`steamTokenId` и получает Steam-логин, пароль и Guard-код. Без worker эти
поля остаются `null`, а `credentialsStatus` равен `NOT_CONNECTED`.

```json
{
  "userId": 123,
  "userApiKey": "youtrade-example",
  "complete": true,
  "account": {
      "steamTokenId": 1703,
      "steamId64": "76561190000000000",
      "givenName": "example",
      "parametersId": 2055,
      "parametersName": "main",
      "source": "CSFLOAT",
      "destination": "MARKET_CSGO",
      "sourceApiKey": null,
      "buyApiKey": "buyer-example",
      "sellApiKey": "seller-example",
      "steamPartner": "123456",
      "steamTradeToken": "example-token",
      "workerTokenId": 301,
      "steamLogin": "example-login",
      "steamPassword": "example-password",
      "guardCode": "ABCDE",
      "credentialsStatus": "AVAILABLE",
      "credentialsError": null
  }
}
```

`complete` равен `true`, только если Steam-данные доступны для выбранного аккаунта.
Если MarketApp сообщает об ошибке worker, его доступные
поля сохраняются, `credentialsStatus` равен `UNAVAILABLE`, а причина находится в
`credentialsError`. Отсутствие worker даёт `NOT_CONNECTED`. Отсутствующий или
чужой аккаунт получает `404`, некорректный `steamTokenId` — `400`, неверный
пароль — `403`, временная блокировка проверки — `429`. Если ответ MarketApp
недоступен или содержит другой либо не единственный `ystId`, весь
запрос завершается `502` без выдачи локальных ключей.

Ответ содержит `Cache-Control: no-store`. Тела запросов и ответов с секретами
нельзя записывать в логи. Guard-код действителен ограниченное время.

## Миграция

Перед развёртыванием этого backend вручную применить
[`flyway/20261007_account_export_password.sql`](../../flyway/20261007_account_export_password.sql)
к `DB_HOST_SECONDARY` (`spring.datasource.listg`). Миграция добавляет хеш,
счётчик неудачных проверок и время окончания блокировки в
`telegram_data_preferences`.
