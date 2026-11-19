import { createContext, useContext } from 'react'
import type { CurrentUser } from '../api/types'
import type {SiteProfile} from '../../../shared/site/profile'

export type AuthState = {
  loading: boolean
  user: CurrentUser | null
  token: string | null
  error: string | null
  profile: SiteProfile | null
  profileError: string | null
  profileMissing: boolean
  refreshProfile(): Promise<SiteProfile | null>
  login(): void
  refresh(): Promise<CurrentUser | null>
  logout(): Promise<void>
}

export const AuthStateContext = createContext<AuthState | null>(null)

export function useAuth() {
  const value = useContext(AuthStateContext)
  if (!value) throw new Error('useAuth должен использоваться внутри AuthProvider')
  return value
}
