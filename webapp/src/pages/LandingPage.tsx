import {Link} from 'react-router-dom'

export function LandingPage() {
    return <main className="landing">
        <nav>
            <div className="brand"><span>YT</span>YouTrade</div>
            <Link className="button secondary" to="/auth">Войти</Link></nav>
        <section className="hero">
            <div className="eyebrow">Торговый кабинет</div>
            <h1>Управляйте торговлей<br/>в одном окне</h1><p>Web-интерфейс для аккаунтов, параметров автоторговли и
            портфеля. Уведомления и подтверждение связи остаются в Telegram.</p>
            <div className="actions"><Link className="button primary" to="/auth">Войти через Telegram</Link><a
                className="button secondary" href="#features">Возможности</a></div>
        </section>
        <section id="features" className="feature-grid">
            <article><b>Аккаунты</b><p>Состояние подключённых торговых аккаунтов.</p></article>
            <article><b>Параметры</b><p>Наборы настроек покупки и продажи.</p></article>
            <article><b>Портфель</b><p>Инвентарь и история операций.</p></article>
        </section>
    </main>
}
