import { endpoints } from './endpoints'
import { get, post } from './client'
import type { AccountInfo, AccountsPage, AuthResponse, CurrentUser, DefaultResponse, LinkTokenResponse, ParameterSet, PortfolioItem, TelegramAuthData, WordItem } from './types'

export const authApi = { login: (data: TelegramAuthData) => post<AuthResponse>(endpoints.auth, data, false), me: () => get<CurrentUser>(endpoints.me), createLinkToken: () => post<LinkTokenResponse>(endpoints.linkToken), botStatus: () => get<CurrentUser>(endpoints.botStatus) }
export const dataApi = {
  accountInfo: () => get<AccountInfo>(endpoints.accountInfo),
  accounts: () => get<AccountsPage>(`${endpoints.accounts}?page=0&size=50`),
  params: () => get<DefaultResponse<ParameterSet[]> | ParameterSet[]>(endpoints.params),
  inventory: () => get<DefaultResponse<PortfolioItem[]> | PortfolioItem[]>(endpoints.inventory),
  includedWords: () => get<DefaultResponse<WordItem[]> | WordItem[]>(endpoints.includedWords),
  excludedWords: () => get<DefaultResponse<WordItem[]> | WordItem[]>(endpoints.excludedWords),
}
