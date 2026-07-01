import { createContext, useContext } from 'react'
import type { CurrentUser, TelegramAuthData } from '../api/types'

export type AuthState = {
  loading: boolean
  user: CurrentUser | null
  token: string | null
  error: string | null
  login(data: TelegramAuthData): Promise<void>
  refresh(): Promise<CurrentUser | null>
  logout(): void
}

export const AuthStateContext = createContext<AuthState | null>(null)

export function useAuth() {
  const value = useContext(AuthStateContext)
  if (!value) throw new Error('useAuth должен использоваться внутри AuthProvider')
  return value
}
