import {useSiteTheme} from './siteThemeState'
import icons from '../../../../shared/site/icons.svg?no-inline'

export function SiteThemeButton() {
    const {theme, toggle} = useSiteTheme()
    return <button className="site-control site-control--quiet site-control--icon site-theme-toggle" type="button" onClick={toggle} title={theme === 'dark' ? 'Светлая тема' : 'Тёмная тема'} aria-label={theme === 'dark' ? 'Включить светлую тему' : 'Включить тёмную тему'}>
        <svg aria-hidden="true"><use href={`${icons}#${theme === 'dark' ? 'sun' : 'moon'}`}/></svg>
    </button>
}
