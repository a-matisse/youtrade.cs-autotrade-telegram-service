import {useSiteTheme} from './siteThemeState'
import icons from '../../../../shared/site/icons.svg?no-inline'

export function SiteThemeButton() {
    const {theme, toggle} = useSiteTheme()
    return <button className="site-theme-toggle" type="button" onClick={toggle} aria-label={theme === 'dark' ? 'Включить светлую тему' : 'Включить тёмную тему'}>
        <svg aria-hidden="true"><use href={`${icons}#${theme === 'dark' ? 'sun' : 'moon'}`}/></svg>
    </button>
}
