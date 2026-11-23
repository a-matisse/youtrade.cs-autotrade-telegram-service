import {useCallback} from 'react'
import {portfolioApi} from '../../api/portfolio'
import type {PortfolioHistory} from '../../api/portfolioTypes'
import {useApiData} from '../../hooks/useApiData'
import {formatTime} from './format'
import {PortfolioAmount} from './PortfolioAmount'
import {PortfolioSection} from './PortfolioSection'

const secondsInDay = 86_400
const stepSeconds = {FIVE_MINUTES: 300, HOURLY: 3600, DAILY: secondsInDay}

function CapitalLine({history}: {history: PortfolioHistory}) {
  const values = history.points.flatMap(point => point.capital.totalCapitalUsd === null ? [] : [point.capital.totalCapitalUsd])
  if (!values.length) return <p>Истории капитала пока нет</p>

  const min = Math.min(...values)
  const max = Math.max(...values)
  const timeRange = history.untilSeconds - history.fromSeconds
  const segments: {x: number; y: number}[][] = []
  let previous: PortfolioHistory['points'][number] | undefined
  for (const point of history.points) {
    const value = point.capital.totalCapitalUsd
    if (value === null) { previous = undefined; continue }
    if (!previous || point.snapshotAt - previous.snapshotAt > Math.max(stepSeconds[previous.resolution], stepSeconds[point.resolution]))
      segments.push([])
    const x = (point.snapshotAt - history.fromSeconds) / timeRange * 98 + 1
    const y = max === min ? 50 : 90 - (value - min) / (max - min) * 80
    segments.at(-1)?.push({x, y})
    previous = point
  }
  return <>
    <div className="portfolio-chart">
      <div className="portfolio-chart-scale">
        <PortfolioAmount value={max}/>
        {max !== min && <PortfolioAmount value={min}/>}
      </div>
      <svg viewBox="0 0 100 100" preserveAspectRatio="none" role="img" aria-label="Изменение оценочного капитала в долларах">
        {segments.map((points, index) => points.length === 1
          ? <circle className="portfolio-chart-point" key={index} cx={points[0].x} cy={points[0].y} r=".6"/>
          : <polyline key={index} points={points.map(({x, y}) => `${x},${y}`).join(' ')} vectorEffect="non-scaling-stroke"/>)}
      </svg>
    </div>
    <div className="portfolio-chart-dates"><span>{formatTime(history.fromSeconds)}</span><span>{formatTime(history.untilSeconds)}</span></div>
  </>
}

export function PortfolioHistoryChart({tdpId, days}: {tdpId: number | null; days: number}) {
  const load = useCallback((signal: AbortSignal) => {
    const until = Math.floor(Date.now() / 1000)
    return portfolioApi.history(tdpId, until - days * secondsInDay, until, signal)
  }, [tdpId, days])
  const query = useApiData(load)
  return <PortfolioSection title="История капитала" {...query}>
    {query.data && <CapitalLine history={query.data}/>}
  </PortfolioSection>
}
