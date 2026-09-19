# Deployment Y.CS

Для первого запуска скопируйте на сервер два файла в один каталог:

```text
install-youtrade.sh
default.env
```

`install-youtrade.sh` уже находится в корне репозитория. Скопируйте на сервер
именно этот файл вместе с `default.env`.

Чтобы не вводить GitHub token при каждом запуске, заполните поле только в
серверной копии установщика:

```bash
SAVED_GITHUB_TOKEN="github_pat_..."
```

Не отправляйте заполненное поле обратно в Git.
Установщик сам клонирует ветку `main` в `/opt/youtradecs` и продолжает работу
из запущенной серверной копии скрипта.

```bash
chmod 700 install-youtrade.sh
sudo ./install-youtrade.sh
```

Во время первого запуска установщик запросит GitHub Personal Access Token.
Токен используется через временный `GIT_ASKPASS`, не записывается в URL remote
и удаляется после получения репозитория.

## Что устанавливается

- новый frontend: `/var/www/youtradecs-landing`;
- старый frontend остаётся в `/var/www/html/dist`;
- исходники и Docker Compose: `/opt/youtradecs`;
- Nginx: `/etc/nginx/sites-available/default`;
- конфигурация `ygame.shop` не изменяется.

Новый сайт доступен на `https://youtradecs.xyz`. Старый frontend доступен на
самом сервере по `http://localhost/old/`. Для просмотра с рабочего компьютера:

```bash
ssh -L 8080:127.0.0.1:80 root@SERVER
```

После подключения откройте `http://localhost:8080/old/`.

Публичный `/old` и все API, кроме четырёх endpoint лендинга, отвечают обычным
`404`. В локальном виртуальном хосте полный `/api/**` старого frontend
проксируется на `127.0.0.1:8080`. Telegram и Heleket webhooks сохраняют прежнюю
маршрутизацию.

## Повторный deployment

Можно повторно запустить внешнюю копию либо актуальный скрипт из checkout:

```bash
sudo /opt/youtradecs/scripts/deploy-production.sh
```

После первой установки скрипт автоматически находит `/opt/youtradecs/default.env`.
Другой исходный env-файл можно указать явно:

```bash
sudo ENV_SOURCE=/root/default.env /opt/youtradecs/scripts/deploy-production.sh
```

Переменные `APP_DIR`, `WEB_ROOT`, `OLD_WEB_ROOT`, `BRANCH` и `ENV_SOURCE`
можно переопределить перед запуском.

После успешной установки сайта выполняется прежняя последовательность Telegram
приложения: `docker system prune -f`, сборка образа, `docker compose down` и
`docker compose up`. Последняя команда остаётся прикреплённой к логам, как в
существующем установщике.
