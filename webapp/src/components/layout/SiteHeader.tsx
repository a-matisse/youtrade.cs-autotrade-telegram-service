import {useEffect, useRef} from 'react'
import logo from '../../assets/brand/youtrade-mark-transparent.png'
import {SiteLink} from './SiteLink'
import {SiteAccountMenu} from './SiteAccountMenu'
import {SiteNavigation} from './SiteNavigation'
import {SiteThemeButton} from './SiteThemeButton'
import '../../../../shared/site/header.css'
import {bindHeaderMenus, closeHeaderMenus} from '../../../../shared/site/menus'

export function SiteHeader({pathname = '/', hash = ''}: {pathname?: string; hash?: string}) {
    const header = useRef<HTMLElement>(null)
    useEffect(() => {
        if (header.current) closeHeaderMenus(header.current)
    }, [pathname, hash])
    useEffect(() => {
        if (header.current) return bindHeaderMenus(header.current)
    }, [])
    const navigation = <SiteNavigation pathname={pathname} hash={hash}/>
    return <header className="site-header" ref={header}>
        <div className="site-header-inner">
            <SiteLink className="site-brand" href="/" aria-label="Y.CS — на главную"><img src={logo} alt=""/><span>Y.CS</span></SiteLink>
            {navigation}
            <div className="site-actions">
                <SiteThemeButton/>
                <SiteAccountMenu pathname={pathname}/>
                <details className="site-mobile-nav"><summary>Меню</summary>{navigation}</details>
            </div>
        </div>
    </header>
}
