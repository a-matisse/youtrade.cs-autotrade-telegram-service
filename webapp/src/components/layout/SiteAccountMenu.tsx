import {useContext} from 'react'
import {AuthStateContext} from '../../auth/authState'
import {SiteLink} from './SiteLink'
import icons from '../../../../shared/site/icons.svg?no-inline'

export function SiteAccountMenu({pathname}: {pathname: string}) {
    const auth = useContext(AuthStateContext)
    if (auth?.loading) return <span className="site-session-status" role="status" aria-label="Проверяем вход">Проверяем вход…</span>
    if (!auth?.user) return <SiteLink className="site-account-link" href="/auth" aria-current={pathname === '/auth' ? 'page' : undefined}>
        <svg aria-hidden="true"><use href={`${icons}#telegram`}/></svg>Войти
    </SiteLink>
    return <details className="site-profile">
        <summary>Кабинет</summary>
        <div className="site-profile-menu">
            <small>Профиль #{auth.user.id}</small>
            <SiteLink href="/dashboard" aria-current={pathname === '/dashboard' ? 'page' : undefined}>Личный кабинет</SiteLink>
            <button type="button" onClick={() => void auth.logout()}>Выйти</button>
        </div>
    </details>
}
