# Маршруты платёжных webhook в Nginx

В `youtrade.cs-autotrade-telegram-service` обновите `docs/nginx-youtradecs.xyz.conf` и соответствующий конфиг на сервере. В HTTPS-блоке `server` для `youtradecs.xyz` найдите два `location`, которые проксируют уведомления на `127.0.0.1:17555`.

## Правка конфигурации

Замените только строки `location`. Сохраните содержимое обоих блоков, включая `proxy_set_header` и обработку `OPTIONS` для Heleket.

```diff
-    location ~ ^/webhook/trybit/notification/?$ {
+    location ~ ^/webhook/trybit/(?:(?:lis-tm|twister-pay)/)?notification/?$ {

-    location = /webhook/heleket {
+    location ~ ^/webhook/heleket(?:/(?:lis-tm|twister-pay))?/?$ {
```

В обоих блоках используйте `proxy_pass http://127.0.0.1:17555;`. Для `location` с регулярным выражением укажите адрес без URI и без завершающего слэша. Так платёжный сервис получит путь целиком, включая идентификатор проекта. [Документация Nginx о proxy_pass](https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_pass)

| Провайдер | Прежний путь | `lis-tm` | `twister-pay` |
| --- | --- | --- | --- |
| Heleket | `/webhook/heleket` | `/webhook/heleket/lis-tm` | `/webhook/heleket/twister-pay` |
| TryBit | `/webhook/trybit/notification` | `/webhook/trybit/lis-tm/notification` | `/webhook/trybit/twister-pay/notification` |

## Выкладка и проверка

1. Проверьте и перечитайте конфигурацию на сервере.

   ```sh
   sudo nginx -t && sudo nginx -s reload
   ```

2. После запуска версии платёжного сервиса из патча отправьте пробный POST с пустым JSON на проектные URL. С обычного IP Heleket вернёт HTTP 400. TryBit для настроенного проекта вернёт HTTP 401. Сверьте запрос с записью в журнале прокси или платёжного сервиса. Пример для `twister-pay`:

   ```sh
   curl -i -H 'Content-Type: application/json' -d '{}' https://youtradecs.xyz/webhook/heleket/twister-pay
   curl -i -H 'Content-Type: application/json' -d '{}' https://youtradecs.xyz/webhook/trybit/twister-pay/notification
   ```

3. После проверки укажите для каждого магазина TryBit его полный проектный URL из таблицы с префиксом `https://youtradecs.xyz`. Heleket получает проектный URL автоматически при создании нового инвойса. Прежние пути нужны для уведомлений по уже выданным инвойсам. [Порядок выкладки платёжного патча](deployment.md)
