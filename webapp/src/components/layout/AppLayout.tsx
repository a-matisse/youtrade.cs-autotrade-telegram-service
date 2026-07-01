import type {ReactNode} from 'react'
import {NavLink} from 'react-router-dom'
import {useAuth} from '../../auth/authState'

const links = [['/dashboard', 'Обзор'], ['/accounts', 'Аккаунты'], ['/portfolio', 'Портфель'], ['/parameters', 'Параметры'], ['/dictionaries', 'Словари']] as const

export function AppLayout({title, subtitle, children}: { title: string; subtitle?: string; children: ReactNode }) {
    const {user, logout} = useAuth()
    return <div className="app-shell">
        <aside className="sidebar"><NavLink className="brand" to="/dashboard"><span>YT</span>YouTrade</NavLink>
            <nav>{links.map(([to, label]) => <NavLink key={to} to={to}>{label}</NavLink>)}</nav>
            <div className="sidebar-bottom"><small>{user?.displayName || user?.username || 'Пользователь'}</small>
                <button className="ghost" onClick={logout}>Выйти</button>
            </div>
        </aside>
        <main className="main-content">
            <header>
                <div><h1>{title}</h1>{subtitle && <p>{subtitle}</p>}</div>
            </header>
            {children}</main>
    </div>
}
