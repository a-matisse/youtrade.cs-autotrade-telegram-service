import {tokenStorage} from '../auth/tokenStorage'

const baseUrl = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')
type Options = RequestInit & { auth?: boolean }
const REQUEST_TIMEOUT_MS = 15_000

export class ApiError extends Error {
    constructor(public status: number, message: string) {
        super(message)
    }
}

export async function apiRequest<T>(path: string, options: Options = {}): Promise<T> {
    const {auth = true, headers, ...init} = options
    const token = tokenStorage.get()
    const controller = new AbortController()
    const timeout = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS)
    let response: Response
    try {
        response = await fetch(`${baseUrl}${path}`, {
            ...init,
            credentials: 'omit',
            referrerPolicy: 'no-referrer',
            signal: controller.signal,
            headers: {Accept: 'application/json', ...(init.body ? {'Content-Type': 'application/json'} : {}), ...headers, ...(auth && token ? {Authorization: `Bearer ${token}`} : {})},
        })
    } catch (error) {
        if (error instanceof DOMException && error.name === 'AbortError') throw new ApiError(408, 'Сервер не ответил вовремя')
        throw error
    } finally {
        window.clearTimeout(timeout)
    }
    if (response.status === 401) {
        tokenStorage.clear();
        window.dispatchEvent(new Event('youtrade:unauthorized'));
        throw new ApiError(401, 'Сессия истекла')
    }
    if (!response.ok) {
        let message = `Ошибка запроса (${response.status})`
        try {
            const body = await response.json() as { message?: string; error?: string };
            message = body.message || body.error || message
        } catch { /* ответ не JSON */
        }
        throw new ApiError(response.status, message)
    }
    if (response.status === 204) return undefined as T
    return response.json() as Promise<T>
}

export const get = <T>(path: string, auth = true) => apiRequest<T>(path, {auth})
export const post = <T>(path: string, body?: unknown, auth = true) => apiRequest<T>(path, {
    method: 'POST',
    body: body === undefined ? undefined : JSON.stringify(body),
    auth
})
