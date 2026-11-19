import {useEffect, useState, type ReactNode} from 'react'
import {applySiteTheme, readSiteTheme, type SiteTheme} from '../../../../shared/site/theme'

import {SiteThemeContext} from './siteThemeState'

export function SiteThemeProvider({children}: {children: ReactNode}) {
    const [theme, setTheme] = useState<SiteTheme>(readSiteTheme)
    useEffect(() => { applySiteTheme(theme) }, [theme])
    useEffect(() => {
        const synchronize = (event: StorageEvent) => {
            if (event.key === 'ycs-theme') setTheme(readSiteTheme())
        }
        window.addEventListener('storage', synchronize)
        return () => window.removeEventListener('storage', synchronize)
    }, [])
    return <SiteThemeContext.Provider value={{theme, toggle: () => setTheme(value => value === 'dark' ? 'light' : 'dark')}}>{children}</SiteThemeContext.Provider>
}
