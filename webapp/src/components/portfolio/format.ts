const usd = new Intl.NumberFormat('ru-RU', {style: 'currency', currency: 'USD'})
const signedUsd = new Intl.NumberFormat('ru-RU', {style: 'currency', currency: 'USD', signDisplay: 'exceptZero'})
const number = new Intl.NumberFormat('ru-RU', {maximumFractionDigits: 2})
const signedNumber = new Intl.NumberFormat('ru-RU', {maximumFractionDigits: 2, signDisplay: 'exceptZero'})
const date = new Intl.DateTimeFormat('ru-RU', {dateStyle: 'short', timeStyle: 'short'})
const markets: Record<string, string> = {
  CSFLOAT: 'CSFloat',
  MARKET_CSGO: 'Market.CS',
  LIS_SKINS: 'LIS-Skins',
  STEAM: 'Steam',
}

export const formatUsd = (value: number, signed: boolean) => (signed ? signedUsd : usd).format(value)
export const formatPercent = (value: number, signed: boolean) => `${(signed ? signedNumber : number).format(value)}%`
export const formatTime = (seconds: number | null) => seconds === null ? '—' : date.format(seconds * 1000)
export const marketName = (market: string | null) => market === null ? '—' : markets[market] ?? market.replaceAll('_', ' ')
