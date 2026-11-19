import {useContext} from 'react'
import {AuthStateContext} from '../../auth/authState'
import {SiteLink} from './SiteLink'
import icons from '../../../../shared/site/icons.svg?no-inline'
import {profileGreeting} from '../../../../shared/site/profile'
import {ProfileAvatar} from './ProfileAvatar'

export function SiteAccountMenu({pathname}: {pathname: string}) {
    const auth = useContext(AuthStateContext)
    if (auth?.loading) return <span className="site-session-status" role="status" aria-label="Проверяем вход">Проверяем вход…</span>
    if (!auth?.user) return <SiteLink className="site-account-link" href="/auth" aria-current={pathname === '/auth' ? 'page' : undefined}>
        <svg aria-hidden="true"><use href={`${icons}#telegram`}/></svg>Войти
    </SiteLink>
    return <details className="site-profile">
        <summary className="site-profile-trigger" aria-label="Открыть меню профиля"><ProfileAvatar profile={auth.profile} refresh={auth.refreshProfile}/></summary>
        <div className="site-profile-menu">
            <p className="site-profile-greeting">{profileGreeting(auth.profile)}</p>
            {auth.profileError && <p className="site-profile-error" role="status">{auth.profileError}</p>}
            {auth.profileMissing && <button type="button" onClick={auth.login}>Войти заново</button>}
            <SiteLink href="/dashboard" aria-current={pathname === '/dashboard' ? 'page' : undefined}>Личный кабинет</SiteLink>
            <button type="button" onClick={() => void auth.logout()}>Выйти</button>
        </div>
    </details>
}
