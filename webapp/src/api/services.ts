import { endpoints } from './endpoints'
import { apiRequest, get, post } from './client'
import type { AccountInfo, AccountsPage, AuthResponse, CurrentUser, DefaultResponse, LandingDeal, LandingDocument, LandingOverview, LinkTokenResponse, ParameterSet, PortfolioItem, SteamCurrencyRate, TelegramAuthData, WordItem } from './types'

export const landingApi = {
  overview: () => get<LandingOverview>(endpoints.overview, false),
  deals: () => get<LandingDeal[]>(endpoints.overviewDeals, false),
  documents: (locale: 'ru' | 'en' | 'ka' = 'ru') => get<LandingDocument[]>(`${endpoints.overviewDocuments}?locale=${locale}`, false),
  steamCurrency: () => get<SteamCurrencyRate[]>(endpoints.steamCurrency, false),
}

export const authApi = { login: (data: TelegramAuthData) => post<AuthResponse>(endpoints.auth, data, false), me: () => get<CurrentUser>(endpoints.me), createLinkToken: () => post<LinkTokenResponse>(endpoints.linkToken), botStatus: () => get<CurrentUser>(endpoints.botStatus) }
export const dataApi = {
  accountInfo: () => get<AccountInfo>(endpoints.accountInfo),
  accounts: () => get<AccountsPage>(`${endpoints.accounts}?page=0&size=50`),
  params: () => get<DefaultResponse<ParameterSet[]> | ParameterSet[]>(endpoints.params),
  inventory: () => get<DefaultResponse<PortfolioItem[]> | PortfolioItem[]>(endpoints.inventory),
  includedWords: () => get<DefaultResponse<WordItem[]> | WordItem[]>(endpoints.includedWords),
  excludedWords: () => get<DefaultResponse<WordItem[]> | WordItem[]>(endpoints.excludedWords),
}

export interface DocumentAcceptance {
  id: number
  type: string
  version: string
  contentHash: string
  acceptedAt: string
  channel: string
}

export const documentApi = {
  acceptances: () => get<{data: DocumentAcceptance[]}>('/api/web/v1/user/p2p/document-acceptances'),
  accept: (document: LandingDocument) => apiRequest<{data: DocumentAcceptance}>('/api/web/v1/user/p2p/document-acceptances', {
    method: 'POST',
    headers: {'Idempotency-Key': crypto.randomUUID()},
    body: JSON.stringify({type: document.type, version: document.version, contentHash: document.contentHash}),
  }),
}