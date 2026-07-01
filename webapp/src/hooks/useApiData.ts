import { useCallback, useEffect, useState } from 'react'
export function useApiData<T>(loader: () => Promise<T>) {
  const [data, setData] = useState<T | null>(null); const [loading, setLoading] = useState(true); const [error, setError] = useState<string | null>(null)
  const reload = useCallback(async () => { setLoading(true); setError(null); try { setData(await loader()) } catch (e) { setError(e instanceof Error ? e.message : 'Неизвестная ошибка') } finally { setLoading(false) } }, [loader])
  useEffect(() => { void reload() }, [reload])
  return { data, loading, error, reload }
}
