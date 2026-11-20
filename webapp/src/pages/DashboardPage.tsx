import {useAuth} from '../auth/authState'
import './AuthPage.css'

export function DashboardPage() {
    const {user} = useAuth()
    return <main className="auth-page">
        <section className="auth-page-content">
            <span className="auth-page-label">Личный кабинет</span>
            <h1>Вы вошли в Y.CS</h1>
            <p>Скоро здесь появится личный кабинет. Пока управление торговлей доступно в Telegram.</p>
            <small>Профиль #{user?.id}</small>
            <div className="auth-page-bot"><a className="site-control site-control--quiet site-control--large" href="https://t.me/youtradecs_bot" target="_blank" rel="noreferrer">Открыть бота</a></div>
        </section>
    </main>
}
