export const endpoints = {
  overview: '/api/web/v1/overview', overviewDeals: '/api/web/v1/overview/deals', overviewDocuments: '/api/web/v1/overview/documents', steamCurrency: '/api/web/v1/steam/currency',
  authTelegram: '/api/web/v1/auth/telegram', authRefresh: '/api/web/v1/auth/refresh',
  authLogout: '/api/web/v1/auth/logout', me: '/api/web/v1/auth/me',
  accountInfo: '/api/web/v1/user/general/info', accounts: '/api/telegram/user/accounts/v2', params: '/api/telegram/user/params/all',
  inventory: '/api/telegram/user/v2/inventory', selling: '/api/telegram/user/v2/selling', buyHistory: '/api/telegram/user/sell/history/buy',
  sellHistory: '/api/telegram/user/sell/history/sell', waiting: '/api/telegram/user/sell/waiting',
  includedWords: '/api/telegram/user/buy/words/included', excludedWords: '/api/telegram/user/buy/words/excluded',
} as const
