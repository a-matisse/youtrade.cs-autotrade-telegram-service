import {useCallback} from 'react'
import {portfolioApi} from '../../api/portfolio'
import type {PortfolioHistory} from '../../api/portfolioTypes'
import {formatTime} from './format'
import {PortfolioAmount} from './PortfolioAmount'
import {PortfolioSection} from './PortfolioSection'
import {usePortfolioData} from './usePortfolioData'

const secondsInDay = 86_400

function historyPath(points: {x: number; y: number}[]) {
  const slopes = points.slice(1).map(({x, y}, index) => {
    const previous = points[index]
    return x === previous.x ? 0 : (y - previous.y) / (x - previous.x)
  })
  const tangents = points.map((_, index) => {
    const before = slopes[index - 1] ?? slopes[index]
    const after = slopes[index] ?? before
    const sign = Math.sign(before)
    return sign === Math.sign(after) ? sign * Math.min(Math.abs(before), Math.abs(after)) : 0
  })
  const format = (coordinate: number) => coordinate.toFixed(3)
  const segments = slopes.map((slope, index) => {
    const from = points[index]
    const to = points[index + 1]
    const end = `${format(to.x)} ${format(to.y)}`
    if (slope === 0) return `L ${end}`

    // Limit the tangents to the adjacent slopes so the curve stays between the two estimates.
    const third = (to.x - from.x) / 3
    const firstControl = `${format(from.x + third)} ${format(from.y + tangents[index] * third)}`
    const secondControl = `${format(to.x - third)} ${format(to.y - tangents[index + 1] * third)}`
    return `C ${firstControl} ${secondControl} ${end}`
  })
  return [`M ${format(points[0].x)} ${format(points[0].y)}`, ...segments].join(' ')
}

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
    return {x, y}
  })

  return <div className="portfolio-chart">
    <div className="portfolio-chart-scale">
      <PortfolioAmount value={max}/>
      {max !== min && <PortfolioAmount value={min}/>}
    </div>
    <svg viewBox="0 0 100 100" preserveAspectRatio="none" role="img" aria-label="Изменение оценочного капитала в долларах">
      {coordinates.length === 1
        ? <circle className="portfolio-chart-point" cx="50" cy="50" r=".6"/>
        : <path d={historyPath(coordinates)} vectorEffect="non-scaling-stroke"/>}
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
