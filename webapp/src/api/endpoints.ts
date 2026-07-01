export const endpoints = {
  auth: '/auth/telegram', me: '/me', linkToken: '/me/telegram-bot/link-token', botStatus: '/me/telegram-bot/status',
  accountInfo: '/api/telegram/user/general/info', accounts: '/api/telegram/user/accounts/v2', params: '/api/telegram/user/params/all',
  inventory: '/api/telegram/user/v2/inventory', selling: '/api/telegram/user/v2/selling', buyHistory: '/api/telegram/user/sell/history/buy',
  sellHistory: '/api/telegram/user/sell/history/sell', waiting: '/api/telegram/user/sell/waiting',
  includedWords: '/api/telegram/user/buy/words/included', excludedWords: '/api/telegram/user/buy/words/excluded',
} as const
