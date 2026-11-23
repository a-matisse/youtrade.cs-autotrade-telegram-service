import { useCallback, useEffect, useState } from 'react'

export function useApiData<T>(loader: (signal: AbortSignal) => Promise<T>) {
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
    void loader(signal).then(data => {
      if (!signal.aborted) setResult({loader, attempt, data, error: null})
    }, cause => {
      if (!signal.aborted) setResult({loader, attempt, data: null,
        error: cause instanceof Error ? cause.message : 'Неизвестная ошибка'})
    })
    return () => controller.abort()
  }, [loader, attempt])

  const current = result?.loader === loader && result.attempt === attempt ? result : null
  return {data: current?.data ?? null, loading: !current, error: current?.error ?? null, reload}
}
