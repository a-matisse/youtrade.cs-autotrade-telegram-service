import {Link} from 'react-router-dom'
import {useAuth} from '../auth/authState'
import logo from '../assets/brand/youtrade-mark-transparent.png'
import './AuthPage.css'

export function DashboardPage() {
    const {user, logout} = useAuth()
    return <main className="auth-page">
        <header className="auth-page-header"><Link className="auth-page-brand" to="/"><img src={logo} alt=""/>Y.CS</Link><button className="auth-page-logout" type="button" onClick={() => void logout()}>Выйти</button></header>
        <section className="auth-page-content">
            <span className="auth-page-label">Личный кабинет</span>
            <h1>Вы вошли в Y.CS</h1>
            <p>Скоро здесь появится личный кабинет. Пока управление торговлей доступно в Telegram.</p>
            <small>Профиль #{user?.id}</small>
            <div className="auth-page-bot"><a href="https://t.me/youtradecs_bot" target="_blank" rel="noreferrer">Открыть бота</a></div>
        </section>
    </main>
}
