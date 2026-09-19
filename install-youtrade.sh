#!/usr/bin/env bash
set -Eeuo pipefail

# ── 01. Параметры ───────────────────────────────────────────────────────────
INSTALLER_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

# Необязательно: вставьте токен между кавычками в серверной копии установщика,
# чтобы не вводить его при каждом запуске. Не коммитьте заполненное поле в Git.
SAVED_GITHUB_TOKEN=""

DEFAULT_ENV_SOURCE="${INSTALLER_DIR}/default.env"
if [[ ! -f "${DEFAULT_ENV_SOURCE}" && -f "${INSTALLER_DIR}/../default.env" ]]; then
  DEFAULT_ENV_SOURCE="${INSTALLER_DIR}/../default.env"
fi
REPO_URL="${REPO_URL:-https://github.com/a-matisse/youtrade.cs-autotrade-telegram-service.git}"
BRANCH="${BRANCH:-main}"
APP_DIR="${APP_DIR:-/opt/youtradecs}"
WEB_ROOT="${WEB_ROOT:-/var/www/youtradecs-landing}"
OLD_WEB_ROOT="${OLD_WEB_ROOT:-/var/www/html/dist}"
NGINX_CONFIG="${NGINX_CONFIG:-/etc/nginx/sites-available/default}"
NGINX_ENABLED="${NGINX_ENABLED:-/etc/nginx/sites-enabled/default}"
ENV_SOURCE="${ENV_SOURCE:-${DEFAULT_ENV_SOURCE}}"
IMAGE_NAME="${IMAGE_NAME:-youtrade.cs-autotrade-telegram-service}"
GITHUB_TOKEN="${GITHUB_TOKEN:-${SAVED_GITHUB_TOKEN}}"

ASKPASS_FILE=""
RELEASE_DIR=""
PREVIOUS_WEB_ROOT=""
NGINX_BACKUP=""
SERVED_FILE=""
HAD_NGINX_CONFIG=0
SITE_COMMITTED=0

cleanup() {
  [[ -z "${ASKPASS_FILE}" || ! -f "${ASKPASS_FILE}" ]] || rm -f -- "${ASKPASS_FILE}"
  [[ -z "${RELEASE_DIR}" || ! -d "${RELEASE_DIR}" ]] || rm -rf -- "${RELEASE_DIR}"
  [[ -z "${SERVED_FILE}" || ! -f "${SERVED_FILE}" ]] || rm -f -- "${SERVED_FILE}"
  if (( ! SITE_COMMITTED )) && [[ -n "${PREVIOUS_WEB_ROOT}" && -e "${PREVIOUS_WEB_ROOT}" && ! -e "${WEB_ROOT}" ]]; then
    mv -- "${PREVIOUS_WEB_ROOT}" "${WEB_ROOT}"
  fi
  unset GITHUB_TOKEN
}
trap cleanup EXIT

rollback_site() {
  echo "Откат frontend и nginx..." >&2
  if (( HAD_NGINX_CONFIG )) && [[ -f "${NGINX_BACKUP}" ]]; then
    cp -- "${NGINX_BACKUP}" "${NGINX_CONFIG}"
  else
    rm -f -- "${NGINX_ENABLED}" "${NGINX_CONFIG}"
  fi
  rm -rf -- "${WEB_ROOT}"
  if [[ -n "${PREVIOUS_WEB_ROOT}" && -e "${PREVIOUS_WEB_ROOT}" ]]; then
    mv -- "${PREVIOUS_WEB_ROOT}" "${WEB_ROOT}"
  fi
  nginx -t && systemctl reload nginx
}

if [[ "${EUID}" -ne 0 ]]; then
  echo "Запустите установщик от root: sudo ./install-youtradecs.sh" >&2
  exit 1
fi
if [[ ! -f /etc/os-release ]] || ! grep -q '^ID=ubuntu$' /etc/os-release; then
  echo "Установщик рассчитан на Ubuntu." >&2
  exit 1
fi
if [[ ! -f "${ENV_SOURCE}" ]]; then
  echo "Не найден ${ENV_SOURCE}. Положите default.env рядом с установщиком или задайте ENV_SOURCE." >&2
  exit 1
fi

# ── 02. Проверка готового сервера ──────────────────────────────────────────
command -v git >/dev/null 2>&1 || { echo "Git не найден." >&2; exit 1; }
command -v curl >/dev/null 2>&1 || { echo "curl не найден." >&2; exit 1; }
command -v rsync >/dev/null 2>&1 || { echo "rsync не найден." >&2; exit 1; }
command -v node >/dev/null 2>&1 || { echo "Node.js не найден." >&2; exit 1; }
command -v npm >/dev/null 2>&1 || { echo "npm не найден." >&2; exit 1; }
command -v nginx >/dev/null 2>&1 || { echo "Nginx не найден." >&2; exit 1; }
command -v docker >/dev/null 2>&1 || { echo "Docker не найден." >&2; exit 1; }
docker compose version >/dev/null 2>&1 || { echo "Docker Compose v2 не найден." >&2; exit 1; }

# ── 03. Получение репозитория ──────────────────────────────────────────────
export GIT_TERMINAL_PROMPT=0
if [[ -z "${GITHUB_TOKEN}" ]]; then
  read -rsp "GitHub Personal Access Token: " GITHUB_TOKEN
  echo
fi
if [[ -z "${GITHUB_TOKEN}" ]]; then
  echo "GitHub token не указан." >&2
  exit 1
fi
export GITHUB_TOKEN
ASKPASS_FILE="$(mktemp)"
chmod 0700 "${ASKPASS_FILE}"
cat > "${ASKPASS_FILE}" <<'ASKPASS'
#!/usr/bin/env bash
case "$1" in
  *Username*) printf '%s\n' 'x-access-token' ;;
  *Password*) printf '%s\n' "${GITHUB_TOKEN}" ;;
esac
ASKPASS
export GIT_ASKPASS="${ASKPASS_FILE}"

if [[ -d "${APP_DIR}/.git" ]]; then
  git -C "${APP_DIR}" fetch origin "${BRANCH}"
  git -C "${APP_DIR}" checkout -B "${BRANCH}" "origin/${BRANCH}"
  git -C "${APP_DIR}" reset --hard "origin/${BRANCH}"
elif [[ -e "${APP_DIR}" ]]; then
  echo "${APP_DIR} существует, но не является Git-репозиторием." >&2
  exit 1
else
  install -d "$(dirname "${APP_DIR}")"
  git clone --branch "${BRANCH}" --single-branch "${REPO_URL}" "${APP_DIR}"
fi

rm -f -- "${ASKPASS_FILE}"
ASKPASS_FILE=""
unset GITHUB_TOKEN GIT_ASKPASS GIT_TERMINAL_PROMPT

# ── 04. Сборка и атомарная публикация лендинга ─────────────────────────────
[[ -f "${APP_DIR}/webapp/package.json" ]] || {
  echo "В ветке ${BRANCH} не найден webapp/package.json. Отправьте лендинг в GitHub." >&2
  exit 1
}
cd "${APP_DIR}/webapp"
npm ci
VITE_API_BASE_URL=https://youtradecs.xyz \
VITE_TELEGRAM_BOT_USERNAME=youtradecs_bot \
VITE_AGENT_FEE_PERCENT="${VITE_AGENT_FEE_PERCENT:-3}" \
VITE_MARKET_FEE_PERCENT="${VITE_MARKET_FEE_PERCENT:-5}" \
npm run build

WEB_PARENT="$(dirname "${WEB_ROOT}")"
install -d -m 0755 "${WEB_PARENT}"
RELEASE_DIR="$(mktemp -d "${WEB_PARENT}/.youtradecs-release.XXXXXX")"
rsync --archive --delete --chmod=D755,F644 "${APP_DIR}/webapp/dist/" "${RELEASE_DIR}/"
[[ -f "${RELEASE_DIR}/index.html" ]] || { echo "Сборка не содержит index.html." >&2; exit 1; }

PREVIOUS_WEB_ROOT="${WEB_ROOT}.previous"
rm -rf -- "${PREVIOUS_WEB_ROOT}"
[[ ! -e "${WEB_ROOT}" ]] || mv -- "${WEB_ROOT}" "${PREVIOUS_WEB_ROOT}"
mv -- "${RELEASE_DIR}" "${WEB_ROOT}"
RELEASE_DIR=""

# ── 05. Nginx: лендинг, четыре API, webhooks и локальный /old ──────────────
[[ -f "${OLD_WEB_ROOT}/index.html" ]] || { rollback_site; echo "Старый frontend не найден: ${OLD_WEB_ROOT}/index.html" >&2; exit 1; }
NGINX_BACKUP="${NGINX_CONFIG}.deploy-backup"
if [[ -f "${NGINX_CONFIG}" ]]; then
  HAD_NGINX_CONFIG=1
  cp -- "${NGINX_CONFIG}" "${NGINX_BACKUP}"
fi
install -m 0644 "${APP_DIR}/docs/nginx-youtradecs.xyz.conf" "${NGINX_CONFIG}"
ln -sfn "${NGINX_CONFIG}" "${NGINX_ENABLED}"
if ! nginx -t; then
  rollback_site
  echo "Новая конфигурация nginx отклонена." >&2
  exit 1
fi
systemctl reload nginx

# ── 06. Проверка публикации и закрытых маршрутов ───────────────────────────
EXPECTED_SHA="$(sha256sum "${WEB_ROOT}/index.html" | awk '{print $1}')"
SERVED_FILE="$(mktemp)"
SERVED_SHA=""
for attempt in {1..15}; do
  if curl --fail --silent --show-error --insecure --resolve 'youtradecs.xyz:443:127.0.0.1' \
    --header 'Cache-Control: no-cache' --output "${SERVED_FILE}" 'https://youtradecs.xyz/offers'; then
    SERVED_SHA="$(sha256sum "${SERVED_FILE}" | awk '{print $1}')"
    [[ "${SERVED_SHA}" != "${EXPECTED_SHA}" ]] || break
  fi
  sleep 1
done
if [[ "${SERVED_SHA}" != "${EXPECTED_SHA}" ]]; then
  rollback_site
  echo "Nginx отдаёт неверный index.html для нового сайта." >&2
  echo "Ожидался SHA-256 ${EXPECTED_SHA}, получен ${SERVED_SHA:-нет ответа}." >&2
  exit 1
fi

for endpoint in overview overview/deals overview/documents steam/currency; do
  if ! curl --fail --silent --show-error --insecure --resolve 'youtradecs.xyz:443:127.0.0.1' \
    --output /dev/null "https://youtradecs.xyz/api/web/v1/${endpoint}"; then
    rollback_site
    echo "Публичный API недоступен: ${endpoint}" >&2
    exit 1
  fi
done

PUBLIC_OLD_STATUS="$(curl --silent --output /dev/null --write-out '%{http_code}' --insecure \
  --resolve 'youtradecs.xyz:443:127.0.0.1' 'https://youtradecs.xyz/old/')"
EXTRA_API_STATUS="$(curl --silent --output /dev/null --write-out '%{http_code}' --insecure \
  --resolve 'youtradecs.xyz:443:127.0.0.1' 'https://youtradecs.xyz/api/private-probe')"
LOCAL_OLD_STATUS="$(curl --silent --output /dev/null --write-out '%{http_code}' \
  --header 'Host: localhost' 'http://127.0.0.1/old/')"
if [[ "${PUBLIC_OLD_STATUS}" != 404 || "${EXTRA_API_STATUS}" != 404 || "${LOCAL_OLD_STATUS}" != 200 ]]; then
  rollback_site
  echo "Проверка доступа не пройдена: public old=${PUBLIC_OLD_STATUS}, extra api=${EXTRA_API_STATUS}, local old=${LOCAL_OLD_STATUS}." >&2
  exit 1
fi

rm -rf -- "${PREVIOUS_WEB_ROOT}"
PREVIOUS_WEB_ROOT=""
rm -f -- "${NGINX_BACKUP}" "${SERVED_FILE}"
SERVED_FILE=""
SITE_COMMITTED=1

# ── 07. Конфигурация Telegram-приложения ──────────────────────────────────
install -m 0600 "${ENV_SOURCE}" "${APP_DIR}/default.env"

# ── 08. Docker deployment Telegram-приложения ─────────────────────────────
cd "${APP_DIR}"
docker system prune -f
docker build -t "${IMAGE_NAME}" .
docker compose down

echo
echo "Новый сайт:      https://youtradecs.xyz/"
echo "Старый сайт:     http://localhost/old (только loopback/SSH tunnel)"
echo "Frontend:        ${WEB_ROOT}"
echo "Telegram app:    docker compose в ${APP_DIR}"
echo "Deployment завершён: ${EXPECTED_SHA}"
echo "Запуск Telegram-приложения; далее отображаются логи Docker Compose."
docker compose up
