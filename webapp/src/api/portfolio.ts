import {apiRequest} from './client'
import {endpoints} from './endpoints'
import type {ListingStatus, PortfolioHistory, PortfolioPerformance, PortfolioPositions, PortfolioSummary, ServiceWallet} from './portfolioTypes'

function portfolioQuery(tdpId: number | null) {
  const query = new URLSearchParams()
  if (tdpId !== null) query.set('tdpId', String(tdpId))
  return query
}

function read<T>(path: string, signal: AbortSignal, query = new URLSearchParams()) {
  return apiRequest<T>(query.size ? `${path}?${query}` : path, {signal, cache: 'no-store'})
}

function dateRangeQuery(tdpId: number | null, from: number, until: number) {
  const query = portfolioQuery(tdpId)
  query.set('from_seconds', String(from))
  query.set('until_seconds', String(until))
  return query
}

export const portfolioApi = {
  summary: (tdpId: number | null, signal: AbortSignal) =>
    read<PortfolioSummary>(endpoints.portfolio, signal, portfolioQuery(tdpId)),
  positions: (tdpId: number | null, status: ListingStatus | '', afterId: number, signal: AbortSignal) => {
    const query = portfolioQuery(tdpId)
    if (status) query.set('status', status)
    query.set('afterId', String(afterId))
    return read<PortfolioPositions>(endpoints.portfolioPositions, signal, query)
  },
  performance: (tdpId: number | null, from: number, until: number, signal: AbortSignal) =>
    read<PortfolioPerformance>(endpoints.performance, signal, dateRangeQuery(tdpId, from, until)),
  history: (tdpId: number | null, from: number, until: number, signal: AbortSignal) =>
    read<PortfolioHistory>(endpoints.portfolioHistory, signal, dateRangeQuery(tdpId, from, until)),
  wallet: (signal: AbortSignal) => read<ServiceWallet>(endpoints.serviceWallet, signal),
}
