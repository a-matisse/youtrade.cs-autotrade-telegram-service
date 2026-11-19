import {siteNavigation} from '../../../../shared/site/navigation'
import {SiteLink} from './SiteLink'

export function SiteNavigation({pathname, hash}: {pathname: string; hash: string}) {
    return <nav className="site-nav" aria-label="Разделы сайта">{siteNavigation.map(item => <SiteLink key={item.href} href={item.href}
        aria-current={item.href === pathname + hash ? 'page' : undefined}>{item.label}</SiteLink>)}</nav>
}
