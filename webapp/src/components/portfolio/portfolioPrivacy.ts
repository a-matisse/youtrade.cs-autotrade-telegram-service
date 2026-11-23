import {createContext, useContext} from 'react'

const privacyKey = 'ycs-hide-finances'

export const PortfolioPrivacyContext = createContext({hidden: false, toggle: () => {}})
export const usePortfolioPrivacy = () => useContext(PortfolioPrivacyContext)

export function readPortfolioPrivacy() {
  try { return sessionStorage.getItem(privacyKey) === 'true' }
  catch { return false }
}

export function savePortfolioPrivacy(hidden: boolean) {
  try { sessionStorage.setItem(privacyKey, String(hidden)) }
  catch { /* The view remains usable when storage is unavailable. */ }
}
