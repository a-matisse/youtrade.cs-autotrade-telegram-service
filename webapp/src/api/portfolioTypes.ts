export interface PortfolioCapital {
  totalCapitalUsd: number | null
  buyAvailableUsd: number | null
  buyPendingUsd: number | null
  saleCashEstimatedUsd: number | null
  inventoryEstimatedUsd: number | null
  oldestCapitalCalculatedAt: number | null
  partial: boolean
  positionCount: number
  valuedPositionCount: number
  matchedOpenCostUsd: number | null
  estimatedNetProceedsUsd: number | null
  estimatedPnlUsd: number | null
  estimatedReturnPercent: number | null
}

export interface PortfolioBreakdown {
  tdpId: number
  name: string
  source: string
  destination: string
  capital: PortfolioCapital
  sharePercent: number | null
  activeBargainCount: number
  offeredAmountUsd: number | null
  marketBalances: {
    market: string
    side: 'BUY' | 'SELL'
    accountId: number
    availableUsd: number | null
    pendingUsd: number | null
    observedAt: number | null
    observedCurrency: string | null
  }[]
}

export interface PortfolioSummary {
  revision: number
  calculatedAt: number
  valuationBasis: string
  capital: PortfolioCapital
  portfolios: PortfolioBreakdown[]
}

export type ListingStatus = 'NOT_LISTED' | 'TO_BE_LISTED' | 'LISTED' | 'DELISTED'

export interface PortfolioPositions {
  nextAfterId: number | null
  positions: {
    positionId: number
    tdpId: number
    itemName: string
    boughtOn: string
    destination: string
    purchasePriceUsd: number | null
    purchaseCostUsd: number | null
    purchasedAt: number
    unlockAt: number
    held: boolean
    listingStatus: ListingStatus
    strategyMinPriceUsd: number | null
    strategyMaxPriceUsd: number | null
    listingPriceUsd: number | null
    marketEstimateUsd: number | null
    estimateCollectedAt: number | null
    estimatedNetProceedsUsd: number | null
    estimatedPnlUsd: number | null
    estimatedReturnPercent: number | null
  }[]
}

export interface PortfolioPerformance {
  fromSeconds: number
  untilSeconds: number
  calculatedAt: number
  tdpId: number | null
  feeBasis: string
  incomeUsd: number | null
  incomePercent: number | null
  saleVolumeUsd: number | null
  matchedCostUsd: number | null
  netProceedsUsd: number | null
  tradeCount: number
  unmatchedCount: number
}

export interface PortfolioHistory {
  fromSeconds: number
  untilSeconds: number
  tdpId: number | null
  fiveMinuteFrom: number
  points: {
    snapshotAt: number
    observedAt: number
    revision: number
    resolution: 'FIVE_MINUTES' | 'HOURLY' | 'DAILY'
    valuationBasis: string
    capital: PortfolioCapital
  }[]
}

export interface ServiceWallet {
  product: 'YOUTRADE_PRO'
  serviceBalanceUsd: number | null
}
