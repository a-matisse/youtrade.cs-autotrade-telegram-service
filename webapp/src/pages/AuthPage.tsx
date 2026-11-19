import {Link} from 'react-router-dom'
import {useAuth} from '../auth/authState'
import logo from '../assets/brand/youtrade-mark-transparent.png'
import './AuthPage.css'

export function AuthPage() {
    const {login, error} = useAuth()
    return <main className="auth-page">
        <header className="auth-page-header"><Link className="auth-page-brand" to="/"><img src={logo} alt=""/>Y.CS</Link><a href="https://t.me/youtradecs_sup">Поддержка</a></header>
        <section className="auth-page-content">
            <span className="auth-page-label">Личный кабинет</span>
            <h1>Вход в Y.CS</h1>
            <p>Один профиль для сайта и Telegram. Подтвердите вход в Telegram — мы откроем ваш кабинет.</p>
            <button className="auth-page-login" type="button" onClick={login}>Войти через Telegram</button>
            <small>При первом входе профиль создаётся автоматически.</small>
            {error && <p className="auth-page-error" role="alert">{error}</p>}
            <div className="auth-page-bot"><b>Управление торговлей — в боте</b><p>Настройка и основные действия сейчас доступны в Telegram.</p><a href="https://t.me/youtradecs_bot" target="_blank" rel="noreferrer">Открыть бота</a></div>
        </section>
    </main>
}
