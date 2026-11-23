import {useApiData} from '../../hooks/useApiData'

const refreshIntervalMs = 5 * 60 * 1000

export function usePortfolioData<T>(loader: (signal: AbortSignal) => Promise<T>) {
  return useApiData(loader, refreshIntervalMs)
}
