import type {SiteTheme} from './theme'

export const siteOrigin = 'https://youtradecs.xyz'
export const learningOrigin = 'https://docs.youtradecs.xyz'

export const siteNavigation = [
  {label: 'Как работает', href: '/#how'},
  {label: 'Тарифы', href: '/#pricing'},
  {label: 'Результаты', href: '/#results'},
  {label: 'Обучение', href: learningOrigin, section: 'learning'},
  {label: 'Документы', href: '/terms', section: 'documents'},
] as const

// Only the public appearance preference crosses domains, never credentials.
export function themedSiteLink(href: string, theme: SiteTheme) {
  if (!href.startsWith('https://')) return href
  const url = new URL(href)
  if (url.origin !== siteOrigin && url.origin !== learningOrigin) return href
  url.searchParams.set('theme', theme)
  return url.toString()
}
