import type {SiteTheme} from './theme'

export const siteOrigin = 'https://youtradecs.xyz'
export const learningOrigin = 'https://docs.youtradecs.xyz'
export const telegramBotUrl = 'https://t.me/youtradecs_bot'
export const telegramGroupUrl = 'https://t.me/youtradecs'
export const siteFooterDescription = 'Автоматизация торговли предметами CS2'
export const siteFooterDisclaimer = 'Y.CS не связан с Valve Corporation. Торговля связана с рыночным риском'

export const siteNavigation = [
  {label: 'Как работает', href: '/#how'},
  {label: 'Тарифы', href: '/#pricing'},
  {label: 'Результаты', href: '/#results'},
] as const

export const siteFooterLinks = [
  {label: 'FAQ', href: learningOrigin + '/faq'},
  {label: 'Обучение', href: learningOrigin},
  {label: 'Оферты и условия', href: '/terms'},
] as const

// Only the public appearance preference crosses domains, never credentials.
export function themedSiteLink(href: string, theme: SiteTheme) {
  if (!href.startsWith('https://')) return href
  const url = new URL(href)
  if (url.origin !== siteOrigin && url.origin !== learningOrigin) return href
  url.searchParams.set('theme', theme)
  return url.toString()
}
