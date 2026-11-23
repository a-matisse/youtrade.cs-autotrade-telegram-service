import {useCallback, useState} from 'react'
import {portfolioApi} from '../api/portfolio'
import type {PortfolioBreakdown, PortfolioSummary} from '../api/portfolioTypes'
import {useNotifications} from '../components/ui/notifications'
import {marketName} from '../components/portfolio/format'
import {PortfolioAmount} from '../components/portfolio/PortfolioAmount'
import {PortfolioCapitalDetails, PortfolioDistribution} from '../components/portfolio/PortfolioCapital'
import {PortfolioHistoryChart} from '../components/portfolio/PortfolioHistoryChart'
import {PortfolioPositions} from '../components/portfolio/PortfolioPositions'
import {PortfolioMetrics, PortfolioSection} from '../components/portfolio/PortfolioSection'
import {usePortfolioData} from '../components/portfolio/usePortfolioData'
import './DashboardPage.css'

const historyPeriods = [[1, '24 часа'], [7, '7 дней'], [30, '30 дней']] as const

const loadOverview = (signal: AbortSignal) => portfolioApi.summary(null, signal)

function portfolioName(portfolio: PortfolioBreakdown) {
  return portfolio.name?.trim() || `${marketName(portfolio.source)} → ${marketName(portfolio.destination)}`
}

function Performance({tdpId}: {tdpId: number | null}) {
  const load = useCallback((signal: AbortSignal) => {
    const midnight = new Date()
    const until = Math.floor(midnight.getTime() / 1000)
    midnight.setHours(0, 0, 0, 0)
    const from = midnight.getTime() / 1000
    return portfolioApi.performance(tdpId, from, Math.max(until, from + 1), signal)
  }, [tdpId])
  const query = usePortfolioData(load)
  const {data} = query
  return <PortfolioSection title="Доходы за сегодня" count={data?.tradeCount} className="portfolio-income" {...query}
    actions={data && <strong className="portfolio-total"><PortfolioAmount value={data.incomeUsd} signed/></strong>}>
    {data && <>
      <PortfolioMetrics values={[
        ['Себестоимость', <PortfolioAmount value={data.matchedCostUsd}/>],
        ['После продажи', <PortfolioAmount value={data.netProceedsUsd}/>],
        ['% от оборота продаж', <PortfolioAmount value={data.incomePercent} percent signed/>],
      ]}/>
      {data.unmatchedCount > 0 && <p className="portfolio-note">Записей без пары цен — {data.unmatchedCount}</p>}
    </>}
  </PortfolioSection>
}

export function DashboardPage() {
  const overview = usePortfolioData(loadOverview)
  const {dismiss} = useNotifications()
  const [selectedTdpId, setSelectedTdpId] = useState<number | null>(null)
  const [historyDays, setHistoryDays] = useState(30)
  const tdpId = overview.data?.portfolios.some(portfolio => portfolio.tdpId === selectedTdpId) ? selectedTdpId : null

  return <main className="portfolio-page">
    <header className="portfolio-header">
      <h1>Личный кабинет</h1>
      <div className="portfolio-filters">
        <label className="portfolio-filter">Портфель
          <select value={tdpId ?? ''} disabled={!overview.data?.portfolios.length} onChange={event => {
            dismiss()
            setSelectedTdpId(event.target.value ? Number(event.target.value) : null)
          }}>
            <option value="">Все портфели</option>
            {overview.data?.portfolios.map(portfolio => <option key={portfolio.tdpId} value={portfolio.tdpId}>
              {portfolioName(portfolio)}
            </option>)}
          </select>
        </label>
        <label className="portfolio-filter">Период
          <select value={historyDays} onChange={event => {
            dismiss()
            setHistoryDays(Number(event.target.value))
          }}>
            {historyPeriods.map(([days, label]) => <option key={days} value={days}>{label}</option>)}
          </select>
        </label>
      </div>
    </header>
    {tdpId === null
      ? <PortfolioContent summary={overview} tdpId={null} historyDays={historyDays}/>
      : <SelectedPortfolio tdpId={tdpId} historyDays={historyDays}/>}
  </main>
}

function SelectedPortfolio({tdpId, historyDays}: {tdpId: number; historyDays: number}) {
  const load = useCallback((signal: AbortSignal) => portfolioApi.summary(tdpId, signal), [tdpId])
  const query = usePortfolioData(load)
  return <PortfolioContent summary={query} tdpId={tdpId} historyDays={historyDays}/>
}

function PortfolioContent({summary, tdpId, historyDays}: {
  summary: ReturnType<typeof usePortfolioData<PortfolioSummary>>
  tdpId: number | null
  historyDays: number
}) {
  const {data} = summary
  return <div className="portfolio-grid">
    <div className="portfolio-charts">
      <PortfolioSection title="Распределение капитала" {...summary}>
        {data && <PortfolioDistribution capital={data.capital}/>}
      </PortfolioSection>
      <PortfolioHistoryChart tdpId={tdpId} days={historyDays}/>
    </div>
    <div className="portfolio-overview">
      <PortfolioSection title="Капитал" {...summary}
        actions={data && <strong className="portfolio-total"><PortfolioAmount value={data.capital.totalCapitalUsd}/></strong>}>
        {data && <PortfolioCapitalDetails summary={data}/>}
      </PortfolioSection>
      <Performance tdpId={tdpId}/>
    </div>
    <PortfolioPositions key={tdpId ?? 'all'} tdpId={tdpId}/>
  </div>
}
