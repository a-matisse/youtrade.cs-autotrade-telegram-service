import {useEffect, useState, type ReactNode} from 'react'
import {useLocation} from 'react-router-dom'
import {SiteHeader} from './SiteHeader'
import {SiteFooter} from './SiteFooter'
import {SiteNotifications} from '../ui/SiteNotifications'
import {PortfolioPrivacyContext, readPortfolioPrivacy, savePortfolioPrivacy} from '../portfolio/portfolioPrivacy'

export function SiteLayout({children}: {children: ReactNode}) {
    const {pathname, hash} = useLocation()
    const [hidden, setHidden] = useState(readPortfolioPrivacy)
    useEffect(() => { savePortfolioPrivacy(hidden) }, [hidden])
    useEffect(() => {
        if (!hash) {window.scrollTo(0, 0); return}
        try { document.getElementById(decodeURIComponent(hash.slice(1)))?.scrollIntoView() }
        catch { /* A malformed fragment must not interrupt navigation. */ }
    }, [pathname, hash])
    return <PortfolioPrivacyContext.Provider value={{hidden, toggle: () => setHidden(value => !value)}}>
        <SiteNotifications>
            <SiteHeader pathname={pathname} hash={hash}/>
            {children}
            <SiteFooter/>
        </SiteNotifications>
    </PortfolioPrivacyContext.Provider>
}
