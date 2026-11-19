import {tokenStorage} from '../auth/tokenStorage'
import {clearSession, getAccessToken} from '../auth/session'
import {ApiError, request} from './request'
export {ApiError} from './request'
type Options = RequestInit & { auth?: boolean }

export async function apiRequest<T>(path: string, options: Options = {}): Promise<T> {
    const {auth = true, headers, ...init} = options
    const token = auth ? await getAccessToken() : null
    if (auth && !token) throw new ApiError(401, 'Войдите через Telegram, чтобы продолжить')
    const send = (accessToken: string | null) => {
        const authHeaders = new Headers(headers)
        if (accessToken) authHeaders.set('Authorization', `Bearer ${accessToken}`)
        return request<T>(path, {...init, headers: authHeaders})
    }
    try {
        return await send(token)
    } catch (error) {
        if (!auth || !(error instanceof ApiError) || error.status !== 401) throw error
        const renewed = await getAccessToken(token ?? undefined)
        if (!renewed) throw new ApiError(401, 'Сессия истекла. Войдите через Telegram ещё раз.')
        if (!['GET', 'HEAD'].includes((init.method ?? 'GET').toUpperCase()))
            throw new ApiError(401, 'Сессия обновлена. Повторите действие.')
        try {
            return await send(renewed)
        } catch (retryError) {
            if (retryError instanceof ApiError && retryError.status === 401 && tokenStorage.get() === renewed)
                clearSession(true)
            throw retryError
        }
    }
}

export const get = <T>(path: string, auth = true) => apiRequest<T>(path, {auth})
export const post = <T>(path: string, body?: unknown, auth = true) => apiRequest<T>(path, {
    method: 'POST',
    body: body === undefined ? undefined : JSON.stringify(body),
    auth
})
