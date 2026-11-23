import { useCallback, useEffect, useState } from 'react'
import {ApiError} from '../api/request'

export function useApiData<T>(loader: (signal: AbortSignal) => Promise<T>, refreshIntervalMs?: number) {
  const [attempt, setAttempt] = useState(0)
  const [result, setResult] = useState<{
    loader: typeof loader
    attempt: number
    data: T | null
    error: string | null
  } | null>(null)
  const reload = useCallback(() => setAttempt(previous => previous + 1), [])

  useEffect(() => {
    const controller = new AbortController()
    const {signal} = controller
    let timer: number | undefined
    let refreshAt: number | null = null

    function scheduleRefresh() {
      window.clearTimeout(timer)
      if (refreshAt === null || signal.aborted || document.hidden) return
      timer = window.setTimeout(() => {
        if (!document.hidden) void load()
      }, Math.max(0, refreshAt - Date.now()))
    }

    async function load() {
      refreshAt = null
      try {
        const data = await loader(signal)
        if (!signal.aborted) setResult({loader, attempt, data, error: null})
      } catch (cause) {
        if (signal.aborted) return
        const error = cause instanceof Error ? cause.message : 'Неизвестная ошибка'
        const retryable = !(cause instanceof ApiError) || [408, 429].includes(cause.status) || cause.status >= 500
        setResult(previous => refreshIntervalMs !== undefined && retryable
          && previous?.loader === loader && previous.attempt === attempt && previous.data != null
          ? previous
          : {loader, attempt, data: null, error})
      } finally {
        if (refreshIntervalMs !== undefined && !signal.aborted) {
          refreshAt = Date.now() + refreshIntervalMs
          scheduleRefresh()
        }
      }
    }

    void load()
    if (refreshIntervalMs !== undefined) document.addEventListener('visibilitychange', scheduleRefresh)
    return () => {
      controller.abort()
      window.clearTimeout(timer)
      document.removeEventListener('visibilitychange', scheduleRefresh)
    }
  }, [loader, attempt, refreshIntervalMs])

  const current = result?.loader === loader && result.attempt === attempt ? result : null
  return {data: current?.data ?? null, loading: !current, error: current?.error ?? null, reload}
}
