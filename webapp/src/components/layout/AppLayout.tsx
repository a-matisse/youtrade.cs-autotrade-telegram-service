import type {ReactNode} from 'react'
import {NavLink} from 'react-router-dom'
import {useAuth} from '../../auth/authState'

const links = [['/dashboard', 'Обзор'], ['/accounts', 'Аккаунты'], ['/portfolio', 'Портфель'], ['/parameters', 'Параметры'], ['/dictionaries', 'Словари']] as const

export function AppLayout({title, subtitle, children, blocked = false}: { title: string; subtitle?: string; children: ReactNode; blocked?: boolean }) {
    const {user, logout} = useAuth()
    return <div className="app-shell">
        <aside className="sidebar"><NavLink className="brand" to="/dashboard"><span>Y</span>Y.CS</NavLink>
            <nav>{(blocked ? links.slice(0, 1) : links).map(([to, label]) => <NavLink key={to} to={to}>{label}</NavLink>)}</nav>
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
