import { useCallback, useEffect, useRef, useState, type ReactNode } from 'react'
import { authApi } from '../api/services'
import type { CurrentUser } from '../api/types'
import { tokenStorage } from './tokenStorage'
import { AuthStateContext } from './authState'
import {clearSession, getAccessToken, getLoginCallbackError, logoutSession, startLogin} from './session'
import {useProfileData} from './useProfileData'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null)
  const [user, setUser] = useState<CurrentUser | null>(null)
  const [loading, setLoading] = useState(true)
  const [callbackError] = useState(getLoginCallbackError)
  const [error, setError] = useState<string | null>(callbackError)
  const pending = useRef<Promise<CurrentUser | null> | null>(null)
  const signingOut = useRef(false)
  const generation = useRef(0)
  const {profile, profileError, profileMissing, refreshProfile: loadProfile, clearProfile} = useProfileData()
  const refresh = useCallback(() => {
    if (callbackError) { setLoading(false); return Promise.resolve(null) }
    if (signingOut.current) return Promise.resolve(null)
    if (pending.current) return pending.current
    const currentGeneration = generation.current
    pending.current = (async () => {
      setError(null)
      try {
        const accessToken = await getAccessToken()
        if (!accessToken) return null
        const current = await authApi.me()
        if (currentGeneration !== generation.current) return null
        await loadProfile(current.id)
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
  }, [loadProfile, callbackError])
  const refreshProfile = useCallback(async () => {
    if (!user || signingOut.current) return null
    try { return await loadProfile(user.id) }
    catch { clearSession(true); return null }
  }, [user, loadProfile])
  const logout = useCallback(async () => {
    if (signingOut.current) return
    signingOut.current = true
    setError(null)
    try { await logoutSession() }
    catch (cause) { setError(cause instanceof Error ? cause.message : 'Не удалось выйти из аккаунта') }
    finally { signingOut.current = false }
  }, [])
  useEffect(() => {
    const onSessionChange = () => {
      const accessToken = tokenStorage.get()
      setToken(accessToken)
      if (!accessToken) { generation.current++; setUser(null); clearProfile() }
      else void refresh()
    }
    window.addEventListener('youtrade:session-change', onSessionChange)
    const checkVisibleSession = () => {if (!document.hidden) void refresh()}
    window.addEventListener('focus', checkVisibleSession)
    document.addEventListener('visibilitychange', checkVisibleSession)
    void refresh()
    return () => {
      window.removeEventListener('youtrade:session-change', onSessionChange)
      window.removeEventListener('focus', checkVisibleSession)
      document.removeEventListener('visibilitychange', checkVisibleSession)
    }
  }, [refresh, clearProfile])
  return <AuthStateContext.Provider value={{ loading, user, token, error, profile, profileError, profileMissing,
    refreshProfile, login: startLogin, refresh, logout }}>{children}</AuthStateContext.Provider>
}
