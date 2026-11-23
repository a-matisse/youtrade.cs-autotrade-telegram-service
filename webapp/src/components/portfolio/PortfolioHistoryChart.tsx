import {useCallback} from 'react'
import {portfolioApi} from '../../api/portfolio'
import type {PortfolioHistory} from '../../api/portfolioTypes'
import {formatTime} from './format'
import {PortfolioAmount} from './PortfolioAmount'
import {PortfolioSection} from './PortfolioSection'
import {usePortfolioData} from './usePortfolioData'

const secondsInDay = 86_400

function CapitalLine({history}: {history: PortfolioHistory}) {
  const observations = history.points.flatMap(({snapshotAt, capital}) => {
    const value = capital.totalCapitalUsd
    if (value === null || snapshotAt < history.fromSeconds || snapshotAt > history.untilSeconds) return []
    return [{snapshotAt, value}]
  })
  if (!observations.length) return <p>Истории капитала пока нет</p>

  const from = observations[0].snapshotAt
  const until = observations[observations.length - 1].snapshotAt
  const timeRange = until - from
  const values = observations.map(({value}) => value)
  const min = Math.min(...values)
  const max = Math.max(...values)
  const coordinates = observations.map(({snapshotAt, value}) => {
    const x = timeRange === 0 ? 50 : (snapshotAt - from) / timeRange * 98 + 1
    const y = max === min ? 50 : 90 - (value - min) / (max - min) * 80
    return `${x},${y}`
  })

  return <div className="portfolio-chart">
    <div className="portfolio-chart-scale">
      <PortfolioAmount value={max}/>
      {max !== min && <PortfolioAmount value={min}/>}
    </div>
    <svg viewBox="0 0 100 100" preserveAspectRatio="none" role="img" aria-label="Изменение оценочного капитала в долларах">
      {coordinates.length === 1
        ? <circle className="portfolio-chart-point" cx="50" cy="50" r=".6"/>
        : <polyline points={coordinates.join(' ')} vectorEffect="non-scaling-stroke"/>}
    </svg>
    <div className="portfolio-chart-dates">
      <span>{formatTime(from)}</span>
      {timeRange > 0 && <span>{formatTime(until)}</span>}
    </div>
  </div>
}

export function PortfolioHistoryChart({tdpId, days}: {tdpId: number | null; days: number}) {
  const load = useCallback((signal: AbortSignal) => {
    const until = Math.floor(Date.now() / 1000)
    return portfolioApi.history(tdpId, until - days * secondsInDay, until, signal)
  }, [tdpId, days])
  const query = usePortfolioData(load)
  return <PortfolioSection title="История капитала" {...query}>
    {query.data && <CapitalLine history={query.data}/>}
  </PortfolioSection>
}
