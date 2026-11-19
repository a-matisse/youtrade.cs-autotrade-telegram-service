import { useCallback, useEffect, useRef, useState, type ReactNode } from 'react'
import { authApi } from '../api/services'
import type { CurrentUser } from '../api/types'
import { tokenStorage } from './tokenStorage'
import { AuthStateContext } from './authState'
import {clearSession, getAccessToken, logoutSession, startLogin} from './session'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null)
  const [user, setUser] = useState<CurrentUser | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const pending = useRef<Promise<CurrentUser | null> | null>(null)
  const generation = useRef(0)
  const refresh = useCallback(() => {
    if (pending.current) return pending.current
    const currentGeneration = generation.current
    pending.current = (async () => {
      setError(null)
      try {
        const accessToken = await getAccessToken()
        if (!accessToken) return null
        const current = await authApi.me()
        if (currentGeneration !== generation.current) return null
        setToken(tokenStorage.get())
        setUser(current)
        return current
      } catch (cause) {
        if (currentGeneration === generation.current) clearSession(true)
        if (!tokenStorage.get()) {
          setError(cause instanceof Error ? cause.message : 'Не удалось проверить сессию')
        }
        return null
      } finally {
        setLoading(false)
        pending.current = null
      }
    })()
    return pending.current
  }, [])
  const logout = useCallback(async () => {
    setError(null)
    try { await logoutSession() }
    catch (cause) { setError(cause instanceof Error ? cause.message : 'Не удалось выйти из аккаунта') }
  }, [])
  useEffect(() => {
    const onSessionChange = () => {
      const accessToken = tokenStorage.get()
      setToken(accessToken)
      if (!accessToken) { generation.current++; setUser(null) }
      else void refresh()
    }
    window.addEventListener('youtrade:session-change', onSessionChange)
    void refresh()
    return () => window.removeEventListener('youtrade:session-change', onSessionChange)
  }, [refresh])
  return <AuthStateContext.Provider value={{ loading, user, token, error, login: startLogin, refresh, logout }}>{children}</AuthStateContext.Provider>
}
