import {useAuth} from '../auth/authState'
import './AuthPage.css'

export function AuthPage() {
    const {login, error, loading} = useAuth()
    return <main className="auth-page">
        <section className="auth-page-content">
            <span className="auth-page-label">Личный кабинет</span>
            <h1>Вход в Y.CS</h1>
            <p>Один профиль для сайта и Telegram. Подтвердите вход в Telegram — мы откроем ваш кабинет.</p>
            <button className="site-control site-control--accent auth-page-login" type="button" disabled={loading} onClick={login}>Войти через Telegram</button>
            <small>При первом входе профиль создаётся автоматически.</small>
            {error && <p className="auth-page-error" role="alert">{error}</p>}
            <div className="auth-page-bot"><b>Управление торговлей — в боте</b><p>Настройка и основные действия сейчас доступны в Telegram.</p><a className="site-control site-control--quiet site-control--large" href="https://t.me/youtradecs_bot" target="_blank" rel="noreferrer">Открыть бота</a></div>
        </section>
    </main>
}
