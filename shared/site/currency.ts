export interface SteamCurrencyRate {
  currency: string
  rate: number
  time: number
  sourceItemname?: string
  sourcePriceTime?: number
  source?: string
}

export function repeatTickerRates(rates: SteamCurrencyRate[]) {
  return rates.length ? Array.from({length: Math.max(1, Math.ceil(12 / rates.length))}, () => rates).flat() : []
}

export function observeSteamRates(onRates: (rates: SteamCurrencyRate[]) => void) {
  const controller = new AbortController()
  const refresh = async () => {
    try {
      const response = await fetch('/api/web/v1/steam/currency', {
        credentials: 'omit',
        headers: {Accept: 'application/json'},
        signal: AbortSignal.any([controller.signal, AbortSignal.timeout(15_000)]),
      })
      if (!response.ok) return
      const data: unknown = await response.json()
      if (Array.isArray(data) && !controller.signal.aborted)
        onRates(data.filter((item): item is SteamCurrencyRate => item != null
          && typeof item.currency === 'string' && Number.isFinite(item.rate) && item.rate > 0))
    } catch { /* Keep the last available public rates if the service is unavailable. */ }
  }
  void refresh()
  const interval = window.setInterval(() => void refresh(), 5 * 60 * 1000)
  return () => {controller.abort(); window.clearInterval(interval)}
}

export function bindTickerVisibility(ticker: HTMLElement) {
  const synchronize = () => ticker.classList.toggle('is-paused', document.hidden)
  synchronize()
  document.addEventListener('visibilitychange', synchronize)
  return () => document.removeEventListener('visibilitychange', synchronize)
}
