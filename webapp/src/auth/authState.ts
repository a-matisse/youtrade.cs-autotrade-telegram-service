import { createContext, useContext } from 'react'
import type { CurrentUser } from '../api/types'

export type AuthState = {
  loading: boolean
  user: CurrentUser | null
  token: string | null
  error: string | null
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
