import {useEffect, type ReactNode} from 'react'
import {useLocation} from 'react-router-dom'
import {SiteHeader} from './SiteHeader'

export function SiteLayout({children}: {children: ReactNode}) {
    const {pathname, hash} = useLocation()
    useEffect(() => {
        if (!hash) {window.scrollTo(0, 0); return}
        try { document.getElementById(decodeURIComponent(hash.slice(1)))?.scrollIntoView() }
        catch { /* A malformed fragment must not interrupt navigation. */ }
    }, [pathname, hash])
    return <><SiteHeader pathname={pathname} hash={hash}/>{children}</>
}
