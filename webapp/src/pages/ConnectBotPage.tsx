import {useCallback, useEffect, useState} from 'react'
import {Navigate} from 'react-router-dom'
import {authApi} from '../api/services'
import {useAuth} from '../auth/authState'

function safeTelegramLink(value: string, expectedBot: string): string | null {
    try {
        const url = new URL(value)
        const botFromPath = url.pathname.split('/').filter(Boolean)[0]
        return url.protocol === 'https:' && url.hostname.toLowerCase() === 't.me' &&
            botFromPath?.toLowerCase() === expectedBot.toLowerCase() ? url.toString() : null
    } catch {
        return null
    }
}

export function ConnectBotPage() {
    const {user, refresh, logout} = useAuth();
    const [link, setLink] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [checking, setChecking] = useState(false)
    const createLink = useCallback(async () => {
        try {
            const result = await authApi.createLinkToken();
            const token = result.linkToken || result.token;
            const bot = import.meta.env.VITE_TELEGRAM_BOT_USERNAME;
            const botName = bot.replace(/^@/, '');
            const validBot = /^[A-Za-z0-9_]{5,32}$/.test(botName) ? botName : null;
            if (!validBot) throw new Error('Некорректно задан VITE_TELEGRAM_BOT_USERNAME');
            const validToken = token && /^[A-Za-z0-9_-]{1,64}$/.test(token) ? token : null;
            const deep = result.deepLink || (validToken ? `https://t.me/${validBot}?start=${encodeURIComponent(validToken)}` : null);
            const safeLink = deep ? safeTelegramLink(deep, validBot) : null;
            if (!safeLink) throw new Error('Backend не вернул безопасную ссылку подключения');
            setLink(safeLink)
        } catch (e) {
            setError(e instanceof Error ? e.message : 'Не удалось создать ссылку')
        }
    }, [])
    useEffect(() => {
        if (!user?.botLinked) void createLink()
    }, [createLink, user?.botLinked])
    useEffect(() => {
        if (user?.botLinked) return;
        const id = window.setInterval(async () => {
            setChecking(true);
            try {
                await refresh()
            } finally {
                setChecking(false)
            }
        }, 5000);
        return () => window.clearInterval(id)
    }, [refresh, user?.botLinked])
    if (user?.botLinked) return <Navigate to="/dashboard" replace/>
    return <main className="auth-screen">
        <section className="auth-card connect-card">
            <div className="icon">✓</div>
            <h1>Подключите Telegram-бота</h1><p>Это обязательный шаг: бот отправляет уведомления и связывает Telegram с
            вашей учётной записью.</p>
            <ol>
                <li>Откройте бота по одноразовой ссылке.</li>
                <li>Нажмите Start в Telegram.</li>
                <li>Вернитесь сюда — статус обновится автоматически.</li>
            </ol>
            {link && <a className="button primary wide" href={link} target="_blank" rel="noreferrer">Открыть
                Telegram-бота</a>}{error && <div className="notice error">{error}
            <button className="ghost" onClick={createLink}>Повторить</button>
        </div>}<p className="muted">{checking ? 'Проверяем подключение…' : 'Проверка каждые 5 секунд'}</p>
            <button className="ghost" onClick={logout}>Выйти</button>
        </section>
    </main>
}
