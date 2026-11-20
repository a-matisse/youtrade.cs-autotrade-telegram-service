import { endpoints } from './endpoints'
import { apiRequest, get } from './client'
import type {SiteProfile} from '../../../shared/site/profile'
import type { AccountInfo, AccountsPage, ConnectedAccount, CurrentUser, DefaultResponse, LandingDeal, LandingDocument, LandingOperations, LandingOverview, ParameterSet, PortfolioItem, SteamCurrencyRate, WordItem } from './types'

export const landingApi = {
  overview: () => get<LandingOverview>(endpoints.overview, false),
  deals: () => get<LandingDeal[]>(endpoints.overviewDeals, false),
  operations: (signal: AbortSignal) => apiRequest<LandingOperations>(endpoints.overviewOperations, {auth: false, signal}),
  documents: (locale: 'ru' | 'en' | 'ka' = 'ru') => get<LandingDocument[]>(`${endpoints.overviewDocuments}?locale=${locale}`, false),
  steamCurrency: () => get<SteamCurrencyRate[]>(endpoints.steamCurrency, false),
}

export const authApi = { me: () => get<CurrentUser>(endpoints.me) }
export const profileApi = { get: () => apiRequest<SiteProfile>(endpoints.profile, {cache: 'no-store'}) }
export const dataApi = {
  accountInfo: () => get<AccountInfo>(endpoints.accountInfo),
  accounts: () => get<AccountsPage<ConnectedAccount>>(`${endpoints.accounts}?page=0&size=50`),
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
