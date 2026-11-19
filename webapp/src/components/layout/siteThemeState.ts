import {createContext, useContext} from 'react'
import type {SiteTheme} from '../../../../shared/site/theme'

export const SiteThemeContext = createContext({theme: 'dark' as SiteTheme, toggle: () => {}})
export const useSiteTheme = () => useContext(SiteThemeContext)
