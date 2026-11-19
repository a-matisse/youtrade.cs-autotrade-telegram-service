export type SiteTheme = 'dark' | 'light'

export function readSiteTheme(): SiteTheme {
  if (typeof window === 'undefined') return 'dark'
  try { return localStorage.getItem('ycs-theme') === 'light' ? 'light' : 'dark' }
  catch { return 'dark' }
}

export function applySiteTheme(theme: SiteTheme) {
  document.documentElement.dataset.siteTheme = theme
  document.documentElement.classList.toggle('dark', theme === 'dark')
  try { localStorage.setItem('ycs-theme', theme) } catch { /* Appearance still works without storage. */ }
}
