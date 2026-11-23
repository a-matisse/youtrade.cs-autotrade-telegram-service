import {useContext} from 'react'
import {useNavigate} from 'react-router-dom'
import {AuthStateContext, useAuth} from '../../auth/authState'
import {SiteLink} from './SiteLink'
import {ProfileAvatar} from './ProfileAvatar'
import {SitePortfolioBalance} from './SitePortfolioBalance'

export function SiteAccountMenu({pathname}: {pathname: string}) {
    const auth = useContext(AuthStateContext)
    if (auth?.loading) return <span className="site-session-status" role="status" aria-label="Проверяем вход">Проверяем вход…</span>
    if (!auth?.user) return <SiteLink className="site-control site-control--solid site-account-link" href="/auth" aria-current={pathname === '/auth' ? 'page' : undefined}>
        Войти
    </SiteLink>
    const inDashboard = pathname === '/dashboard'
    return <div className={inDashboard ? 'site-account site-account--portfolio' : 'site-account'}>
        {inDashboard && <SitePortfolioBalance/>}
        <SiteProfileMenu inDashboard={inDashboard}/>
    </div>
}

function SiteProfileMenu({inDashboard}: {inDashboard: boolean}) {
    const auth = useAuth()
    const navigate = useNavigate()

    return <details className="site-profile">
        <summary className="site-control site-control--quiet site-control--icon site-profile-trigger"
            aria-label={inDashboard ? 'Открыть меню профиля' : 'Перейти в личный кабинет'}
            onClick={event => {
                if (inDashboard) return
                event.preventDefault()
                navigate('/dashboard')
            }}>
            <ProfileAvatar profile={auth.profile} refresh={auth.refreshProfile}/>
        </summary>
        <div className="site-profile-menu">
            {auth.profileError && <p className="site-profile-error" role="status">{auth.profileError}</p>}
            {auth.profileMissing && <button className="site-control site-control--quiet" type="button" onClick={auth.login}>Войти заново</button>}
            <SiteLink className="site-control site-control--quiet" href="/dashboard" aria-current={inDashboard ? 'page' : undefined}>Личный кабинет</SiteLink>
            <button className="site-control site-control--quiet" type="button" onClick={() => void auth.logout()}>Выйти</button>
        </div>
    </details>
}
