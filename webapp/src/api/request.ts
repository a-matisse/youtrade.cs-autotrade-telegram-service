const baseUrl = (import.meta.env.VITE_API_BASE_URL || (import.meta.env.DEV ? '' : 'https://youtradecs.xyz')).replace(/\/$/, '')
const REQUEST_TIMEOUT_MS = 15_000

export class ApiError extends Error {
  constructor(public status: number, message: string) { super(message) }
}

export async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const controller = new AbortController()
  const timeout = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS)
  const headers = new Headers(options.headers)
  headers.set('Accept', 'application/json')
  if (options.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json')
  // Authentication and authenticated user data stay on the origin that owns the session.
  const url = path.startsWith('/api/web/v1/auth/') || headers.has('Authorization') ? path : `${baseUrl}${path}`
  try {
    const response = await fetch(url, {...options, headers, credentials: 'same-origin',
      referrerPolicy: 'no-referrer', signal: controller.signal})
    if (!response.ok) {
      let message = `Ошибка запроса (${response.status})`
      try {
        const body = await response.json() as {message?: string; error?: string}
        message = body.message || body.error || message
      } catch { /* The response may be HTML from a proxy. */ }
      throw new ApiError(response.status, message)
    }
    return response.status === 204 ? undefined as T : await response.json() as T
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') throw new ApiError(408, 'Сервер не ответил вовремя')
    throw error
  } finally {
    window.clearTimeout(timeout)
  }
}
