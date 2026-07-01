import { useCallback, useEffect, useState, type ReactNode } from 'react'
import { authApi } from '../api/services'
import type { CurrentUser, TelegramAuthData } from '../api/types'
import { tokenStorage } from './tokenStorage'
import { AuthStateContext } from './authState'

function extractToken(value: { token?: string; jwt?: string; accessToken?: string }) {
  const token = value.token || value.jwt || value.accessToken
  return token && token.length <= 8192 && /^[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+$/.test(token) ? token : undefined
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState(tokenStorage.get())
  const [user, setUser] = useState<CurrentUser | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const logout = useCallback(() => { tokenStorage.clear(); setToken(null); setUser(null); setError(null) }, [])
  const refresh = useCallback(async () => { if (!tokenStorage.get()) { setUser(null); return null } try { const current = await authApi.me(); setUser(current); return current } catch (e) { setError(e instanceof Error ? e.message : 'Не удалось проверить сессию'); return null } }, [])
  const login = useCallback(async (data: TelegramAuthData) => { setError(null); const result = await authApi.login(data); const nextToken = extractToken(result); if (!nextToken) throw new Error('Backend не вернул JWT'); tokenStorage.set(nextToken); setToken(nextToken); const current = await authApi.me(); setUser(current) }, [])
  useEffect(() => { void refresh().finally(() => setLoading(false)); const onUnauthorized = () => logout(); window.addEventListener('youtrade:unauthorized', onUnauthorized); return () => window.removeEventListener('youtrade:unauthorized', onUnauthorized) }, [logout, refresh])
  return <AuthStateContext.Provider value={{ loading, user, token, error, login, refresh, logout }}>{children}</AuthStateContext.Provider>
}
