import {useInRouterContext, Link} from 'react-router-dom'
import type {AnchorHTMLAttributes} from 'react'
import {themedSiteLink} from '../../../../shared/site/navigation'
import {useSiteTheme} from './siteThemeState'

export function SiteLink({href = '/', children, ...props}: AnchorHTMLAttributes<HTMLAnchorElement>) {
    const router = useInRouterContext()
    const {theme} = useSiteTheme()
    if (router && href.startsWith('/') && !href.startsWith('//')) return <Link to={href} {...props}>{children}</Link>
    return <a href={themedSiteLink(href, theme)} {...props}>{children}</a>
}
