import {useAuth} from '../auth/authState'
import {useLocation} from 'react-router-dom'
import './AuthPage.css'

export function AuthPage({completing = false}: {completing?: boolean}) {
    const {login, error, loading} = useAuth()
    const {state} = useLocation()
    const loginError = error || (typeof state?.loginError === 'string' ? state.loginError : null)
    return <main className="auth-page">
        <section className="auth-page-content">
            <span className="auth-page-label">Личный кабинет</span>
            <h1>Вход в Y.CS</h1>
            <p>Один профиль для сайта и Telegram. Подтвердите вход в Telegram — мы откроем ваш кабинет.</p>
            <button className="site-control site-control--accent auth-page-login" type="button" disabled={loading || completing} onClick={login}>{completing ? 'Завершаем вход…' : 'Войти через Telegram'}</button>
            {completing && <div className="auth-page-progress" role="status"><div className="site-wait-line" aria-hidden="true"/><p>Проверяем вход и открываем ваш кабинет…</p></div>}
            <small>При первом входе профиль создаётся автоматически.</small>
            {!completing && loginError && <p className="auth-page-error" role="alert">{loginError}</p>}
            <div className="auth-page-bot"><b>Управление торговлей — в боте</b><p>Настройка и основные действия сейчас доступны в Telegram.</p><a className="site-control site-control--quiet site-control--large" href="https://t.me/youtradecs_bot" target="_blank" rel="noreferrer">Открыть бота</a></div>
        </section>
    </main>
}
