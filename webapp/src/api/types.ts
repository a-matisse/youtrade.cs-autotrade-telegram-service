export interface TelegramAuthData { id: number; first_name: string; last_name?: string; username?: string; photo_url?: string; auth_date: number; hash: string }
export interface AuthResponse { token?: string; jwt?: string; accessToken?: string }
export interface CurrentUser { id: string | number; displayName?: string; username?: string; botLinked: boolean }
export interface LinkTokenResponse { linkToken?: string; token?: string; deepLink?: string; expiresAt?: string }
export interface DefaultResponse<T> { data?: T; result?: T; success?: boolean; error?: string; message?: string }
export interface AccountInfo { tdId?: number; tdpId?: number; qualified?: boolean; balance?: number | string; referralBalance?: number | string; blockedUntil?: string | null; bargainAllowed?: boolean; bargainAllowedUntil?: string | null; givenName?: string }
export interface ParameterSet { tdpId: number; givenName: string; source: string; destination: string; balance?: number | string }
export interface AccountsPage<T = unknown> { content?: T[]; data?: T[]; items?: T[]; page?: number; totalPages?: number; totalElements?: number }
export interface PortfolioItem { id?: number; itemId?: number; name?: string; title?: string; price?: number | string; buyPrice?: number | string; sellPrice?: number | string; status?: string; [key: string]: unknown }
export interface WordItem { id: number; word?: string; value?: string }

export interface WorkerPriceData { accCount: number; periodDays: number; price: number }
export interface LandingPricing {
  buySubPrices: Record<string, number>
  bargainBuySubPrices: Record<string, number>
  sellSubPrices: Record<string, number>
  workerPriceData: WorkerPriceData
  currency?: number
}
export interface LandingDailyStatistics { date: string; sales: number; cost: number; revenue: number }
export interface LandingStatistics {
  purchases: number
  purchaseVolume: number
  sales: number
  cost: number
  revenue: number
  daily: LandingDailyStatistics[]
}
export interface LandingOverview {
  brand: string
  telegramBotUrl: string
  pricing: LandingPricing
  statistics: LandingStatistics
  generatedAt: string
}
export interface SteamCurrencyRate {
  currency: string
  rate: number
  time: number
  sourceItemname?: string
  sourcePriceTime?: number
  source?: string
}
export interface LandingDeal {
  itemName: string
  imageUrl: string | null
  soldAt: string
  source: string
  destination: string
  cost: number
  revenue: number
  profit: number
}
export interface LandingDocument {
  type: 'CLIENT_EXCHANGE_TERMS' | 'OPERATOR_EXCHANGE_TERMS' | 'YCS_SERVICE_TERMS' | string
  version: string
  locale: string
  contentHash: string
  content: string
  published: boolean
}
