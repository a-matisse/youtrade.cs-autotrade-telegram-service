export interface TelegramAuthData { id: number; first_name: string; last_name?: string; username?: string; photo_url?: string; auth_date: number; hash: string }
export interface AuthResponse { token?: string; jwt?: string; accessToken?: string }
export interface CurrentUser { id: string | number; displayName?: string; username?: string; botLinked: boolean }
export interface LinkTokenResponse { linkToken?: string; token?: string; deepLink?: string; expiresAt?: string }
export interface DefaultResponse<T> { data?: T; result?: T; success?: boolean; error?: string; message?: string }
export interface AccountInfo { tdId?: number; tdpId?: number; qualified?: boolean; balance?: number | string; givenName?: string }
export interface ParameterSet { tdpId: number; givenName: string; source: string; destination: string; balance?: number | string }
export interface AccountsPage<T = unknown> { content?: T[]; data?: T[]; items?: T[]; page?: number; totalPages?: number; totalElements?: number }
export interface PortfolioItem { id?: number; itemId?: number; name?: string; title?: string; price?: number | string; buyPrice?: number | string; sellPrice?: number | string; status?: string; [key: string]: unknown }
export interface WordItem { id: number; word?: string; value?: string }
