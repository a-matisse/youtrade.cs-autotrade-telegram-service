import {useAuth} from '../auth/authState'
import {useLocation} from 'react-router-dom'
import {useEffect} from 'react'
import {useNotifications} from '../components/ui/notifications'
import telegramLogo from '../assets/brand/telegram.svg'
import './AuthPage.css'

export function AuthPage({completing = false}: {completing?: boolean}) {
    const {login, error, loading} = useAuth()
    const {state, key} = useLocation()
    const loginError = error || (typeof state?.loginError === 'string' ? state.loginError : null)
    const notifications = useNotifications()
    useEffect(() => {
        if (!completing && loginError) notifications.error(loginError, 'Не удалось войти')
    }, [completing, loginError, key, notifications])
    return <main className="auth-page auth-page--split">
        <aside className="auth-page-intro">
            <span className="auth-page-label">Y.CS / Автоматизация торговли</span>
            <h2>Настройте один раз.<br/>Наблюдайте.</h2>
            <p>Покупка, ожидание и продажа — в одном торговом цикле.</p>
            <div className="auth-page-cycle" aria-hidden="true"><span>Покупка</span><span>Ожидание</span><span>Продажа</span></div>
        </aside>
        <section className="auth-page-content">
            <h1>Приветствуем в Y.CS</h1>
            <p className="auth-page-method-label">Войти с помощью</p>
            <button className="site-control site-control--solid auth-page-login" type="button" disabled={loading || completing} onClick={login}>
                <img src={telegramLogo} width="24" height="24" alt=""/>
                {completing ? 'Завершаем вход…' : 'Telegram'}
            </button>
            {completing && <div className="auth-page-progress" role="status"><div className="site-wait-line" aria-hidden="true"/><p>Проверяем вход и открываем ваш кабинет…</p></div>}
        </section>
    </main>
}
