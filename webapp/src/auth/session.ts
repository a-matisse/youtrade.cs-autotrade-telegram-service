import {ApiError, request} from '../api/request'
import {endpoints} from '../api/endpoints'
import type {AuthResponse} from '../api/types'
import {tokenStorage} from './tokenStorage'

type SessionMessage = {type: 'session'; accessToken: string; expiresAt: number; requestId?: string; login?: boolean}
  | {type: 'cleared'; blocked: boolean}
  | {type: 'request-session'; requestId: string}

let channel: BroadcastChannel | null = null
let pending: Promise<string | null> | null = null
let logoutPending: Promise<void> | null = null
let initialized = false
let blocked = false
let guest = false
let callbackPending = false
const peers = new Map<string, () => void>()

export function getLoginCallbackError(): string | null {
  if (window.location.pathname !== '/auth/callback') return null
  const error = new URLSearchParams(window.location.search).get('error')
  if (error === null) return null
  switch (error) {
    case 'access_denied': return 'Вход через Telegram отменён. Попробуйте ещё раз, когда будете готовы.'
    case 'login_expired': return 'Время подтверждения истекло. Начните вход через Telegram заново.'
    case 'temporarily_unavailable': return 'Вход через Telegram временно недоступен. Попробуйте немного позже.'
    default: return 'Не удалось подтвердить вход через Telegram. Попробуйте войти ещё раз.'
  }
}

function notify() {
  window.dispatchEvent(new Event('youtrade:session-change'))
}

function validToken() {
  return tokenStorage.get() !== null && tokenStorage.expiresAt() > Date.now() + 10_000
}

function initialize() {
  if (initialized) return
  initialized = true
  callbackPending = window.location.pathname === '/auth/callback'
  tokenStorage.clear()
  if (!('BroadcastChannel' in window)) return
  channel = new BroadcastChannel('ycs-auth')
  channel.onmessage = ({data}: MessageEvent<SessionMessage>) => {
    if (data?.type === 'request-session') {
      if (!blocked && validToken()) channel?.postMessage({type: 'session', requestId: data.requestId,
        accessToken: tokenStorage.get(), expiresAt: tokenStorage.expiresAt()})
    } else if (data?.type === 'session') {
      if ((blocked && !data.login) || typeof data.accessToken !== 'string' || !Number.isFinite(data.expiresAt)
        || data.expiresAt <= Date.now() + 10_000) return
      peers.get(data.requestId ?? '')?.()
      if (tokenStorage.expiresAt() > data.expiresAt) return
      const changed = tokenStorage.get() !== data.accessToken
      blocked = false
      tokenStorage.set(data.accessToken, data.expiresAt)
      guest = false
      if (changed) notify()
    } else if (data?.type === 'cleared') clearSession(data.blocked, false)
  }
}

export function clearSession(preventRefresh = false, broadcast = true) {
  blocked = preventRefresh
  guest = true
  tokenStorage.clear()
  if (broadcast) channel?.postMessage({type: 'cleared', blocked})
  notify()
}

async function synchronizeTabs() {
  if (!channel) return
  const requestId = crypto.randomUUID()
  await new Promise<void>(resolve => {
    const complete = () => { window.clearTimeout(timer); peers.delete(requestId); resolve() }
    const timer = window.setTimeout(complete, 80)
    peers.set(requestId, complete)
    channel?.postMessage({type: 'request-session', requestId})
  })
}

async function locked<T>(action: () => Promise<T>): Promise<T> {
  if (!navigator.locks || !channel)
    throw new Error('Для безопасного входа обновите браузер до актуальной версии.')
  return navigator.locks.request('ycs-auth-session', action)
}

async function rotate(): Promise<string | null> {
  try {
    const response = await request<AuthResponse>(endpoints.authRefresh, {method: 'POST'})
    if (typeof response.accessToken !== 'string' || response.tokenType !== 'Bearer'
      || !Number.isFinite(response.expiresIn) || response.expiresIn <= 0)
      throw new Error('Некорректный ответ авторизации')
    const expiresAt = Date.now() + response.expiresIn * 1000
    tokenStorage.set(response.accessToken, expiresAt)
    guest = false
    blocked = false
    channel?.postMessage({type: 'session', accessToken: response.accessToken, expiresAt,
      login: window.location.pathname === '/auth/callback'})
    notify()
    return response.accessToken
  } catch (error) {
    // A lost response may already have rotated the cookie. Never retry it.
    clearSession(true)
    if (error instanceof ApiError && error.status === 401) return null
    if (error instanceof ApiError && error.status === 403)
      throw new Error('Вход недоступен. Проверьте доступ к аккаунту или обратитесь в поддержку.')
    throw new Error('Не удалось восстановить вход. Войдите через Telegram ещё раз.')
  }
}

export function getAccessToken(rejectedToken?: string): Promise<string | null> {
  initialize()
  if (logoutPending || blocked || guest) return Promise.resolve(null)
  if (pending) return pending
  if (!callbackPending && validToken() && (!rejectedToken || rejectedToken !== tokenStorage.get()))
    return Promise.resolve(tokenStorage.get())
  pending = locked(async () => {
    // The callback owns a new cookie; a peer may still have the previous login.
    if (callbackPending) { callbackPending = false; return rotate() }
    await synchronizeTabs()
    if (blocked || guest) return null
    if (validToken() && (!rejectedToken || rejectedToken !== tokenStorage.get())) return tokenStorage.get()
    return rotate()
  }).finally(() => { pending = null })
  return pending
}

export function startLogin() {
  window.location.assign(endpoints.authTelegram)
}

export function logoutSession(): Promise<void> {
  if (logoutPending) return logoutPending
  const logout = async () => {
    initialize()
    await pending
    await locked(async () => {
      await synchronizeTabs()
      if (blocked || guest) return
      let accessToken = validToken() ? tokenStorage.get() : await rotate()
      if (!accessToken) return
      try {
        await request<void>(endpoints.authLogout, {method: 'POST', headers: {Authorization: `Bearer ${accessToken}`}})
      } catch (error) {
        if (!(error instanceof ApiError) || error.status !== 401) throw error
        accessToken = await rotate()
        if (accessToken) await request<void>(endpoints.authLogout,
          {method: 'POST', headers: {Authorization: `Bearer ${accessToken}`}})
      }
    })
  }
  logoutPending = logout().catch(() => {
    throw new Error('Не удалось подтвердить выход на сервере. Для повторного входа используйте Telegram.')
  }).finally(() => {
    clearSession(true)
    logoutPending = null
  })
  return logoutPending
}
