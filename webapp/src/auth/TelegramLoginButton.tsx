import { useEffect, useRef } from 'react'
import type { TelegramAuthData } from '../api/types'

declare global { interface Window { onTelegramAuth?: (user: TelegramAuthData) => void } }

function isTelegramAuthData(value: unknown): value is TelegramAuthData {
  if (!value || typeof value !== 'object') return false
  const data = value as Partial<TelegramAuthData>
  const now = Math.floor(Date.now() / 1000)
  return Number.isSafeInteger(data.id) && typeof data.first_name === 'string' && data.first_name.length <= 128 &&
    Number.isSafeInteger(data.auth_date) && Math.abs(now - (data.auth_date ?? 0)) <= 600 &&
    typeof data.hash === 'string' && /^[a-f\d]{64}$/i.test(data.hash)
}

export function TelegramLoginButton({ onAuth }: { onAuth: (data: TelegramAuthData) => void }) {
  const host = useRef<HTMLDivElement>(null)
  useEffect(() => {
    const node = host.current; if (!node) return
    const bot = import.meta.env.VITE_TELEGRAM_LOGIN_BOT_NAME
    if (!/^[A-Za-z0-9_]{5,32}$/.test(bot)) return
    window.onTelegramAuth = (data: TelegramAuthData) => { if (isTelegramAuthData(data)) onAuth(data) }
    const script = document.createElement('script'); script.async = true; script.src = 'https://telegram.org/js/telegram-widget.js?22'; script.referrerPolicy = 'no-referrer'; script.setAttribute('data-telegram-login', bot); script.setAttribute('data-size', 'large'); script.setAttribute('data-radius', '10'); script.setAttribute('data-onauth', 'onTelegramAuth(user)'); node.appendChild(script)
    return () => { delete window.onTelegramAuth; node.replaceChildren() }
  }, [onAuth])
  if (!/^[A-Za-z0-9_]{5,32}$/.test(import.meta.env.VITE_TELEGRAM_LOGIN_BOT_NAME)) return <div className="notice warning">Укажите корректный VITE_TELEGRAM_LOGIN_BOT_NAME для отображения Telegram Login Widget.</div>
  return <div ref={host} className="telegram-widget" />
}
