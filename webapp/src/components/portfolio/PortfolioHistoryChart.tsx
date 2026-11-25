import {useCallback, useMemo, useState, type PointerEvent} from 'react'
import {portfolioApi} from '../../api/portfolio'
import type {PortfolioHistory} from '../../api/portfolioTypes'
import {formatTime} from './format'
import {PortfolioAmount} from './PortfolioAmount'
import {PortfolioSection} from './PortfolioSection'
import {usePortfolioData} from './usePortfolioData'

const secondsInDay = 86_400
const maxCornerInset = 4
const plotStartX = 1
const plotEndX = 99
const plotTopY = 10
const plotBottomY = 90

type ChartPoint = {x: number; y: number}
type ChartSegment = {from: ChartPoint; to: ChartPoint; control?: ChartPoint}

function insetCorner(corner: ChartPoint, toward: ChartPoint) {
  const dx = toward.x - corner.x
  const dy = toward.y - corner.y
  const ratio = Math.min(.45, maxCornerInset / Math.hypot(dx, dy))
  return {x: corner.x + dx * ratio, y: corner.y + dy * ratio}
}

function historyCurve(points: ChartPoint[]) {
  const distinct = points.filter(({x, y}, index) => x !== points[index - 1]?.x || y !== points[index - 1]?.y)
  // Only the ends of each plateau affect the shape of the line.
  const vertices = distinct.filter(({y}, index) => y !== distinct[index - 1]?.y || y !== distinct[index + 1]?.y)
  const segments: ChartSegment[] = []
  let from = vertices[0]
  vertices.slice(1, -1).forEach((corner, index) => {
    const entry = insetCorner(corner, vertices[index])
    const exit = insetCorner(corner, vertices[index + 2])
    segments.push({from, to: entry}, {from: entry, to: exit, control: corner})
    from = exit
  })
  segments.push({from, to: vertices[vertices.length - 1]})

  const format = ({x, y}: ChartPoint) => `${x.toFixed(3)} ${y.toFixed(3)}`
  const commands = segments.map(({to, control}) => control
    ? `Q ${format(control)} ${format(to)}`
    : `L ${format(to)}`)
  return {segments, path: [`M ${format(vertices[0])}`, ...commands].join(' ')}
}

function quadratic(from: number, control: number, to: number, progress: number) {
  const remaining = 1 - progress
  return remaining ** 2 * from + 2 * remaining * progress * control + progress ** 2 * to
}

function pointOnCurve(segments: ChartSegment[], x: number): ChartPoint {
  let left = 0
  let right = segments.length - 1
  while (left < right) {
    const middle = Math.floor((left + right) / 2)
    if (segments[middle].to.x < x) left = middle + 1
    else right = middle
  }

  const {from, to, control} = segments[left]
  if (from.x === to.x) return to
  if (!control) return {x, y: from.y + (x - from.x) / (to.x - from.x) * (to.y - from.y)}

  let start = 0
  let end = 1
  for (let step = 0; step < 14; step++) {
    const progress = (start + end) / 2
    if (quadratic(from.x, control.x, to.x, progress) < x) start = progress
    else end = progress
  }
  return {x, y: quadratic(from.y, control.y, to.y, (start + end) / 2)}
}

function prepareHistory(history: PortfolioHistory) {
  const observations = history.points.flatMap(({snapshotAt, capital}) => {
    const value = capital.totalCapitalUsd
    if (value === null || snapshotAt < history.fromSeconds || snapshotAt > history.untilSeconds) return []
    return [{snapshotAt, value}]
  })
  if (!observations.length) return null

  const from = observations[0].snapshotAt
  const until = observations[observations.length - 1].snapshotAt
  const timeRange = until - from
  const values = observations.map(({value}) => value)
  const min = Math.min(...values)
  const max = Math.max(...values)
  const points = observations.map(({snapshotAt, value}) => {
    const x = timeRange === 0 ? 50 : plotStartX + (snapshotAt - from) / timeRange * (plotEndX - plotStartX)
    const y = max === min ? 50 : plotBottomY - (value - min) / (max - min) * (plotBottomY - plotTopY)
    return {x, y}
  })
  const lastPoint = points[points.length - 1]
  const curve = timeRange === 0 && max === min ? null : historyCurve(points)
  return {from, until, min, max, lastPoint, curve}
}

function CapitalLine({history}: {history: PortfolioHistory}) {
  const [hoveredX, setHoveredX] = useState<number | null>(null)
  const chart = useMemo(() => prepareHistory(history), [history])
  if (!chart) return <p>Истории капитала пока нет</p>

  const {from, until, min, max, lastPoint, curve} = chart
  const point = hoveredX === null ? null : from === until || !curve ? lastPoint : pointOnCurve(curve.segments, hoveredX)
  const value = point ? min + (plotBottomY - point.y) / (plotBottomY - plotTopY) * (max - min) : null
  const time = point && from !== until ? from + (point.x - plotStartX) / (plotEndX - plotStartX) * (until - from) : from
  const inspect = (event: PointerEvent<HTMLDivElement>) => {
    const {left, width} = event.currentTarget.getBoundingClientRect()
    if (width > 0) setHoveredX(Math.max(plotStartX, Math.min(plotEndX, (event.clientX - left) / width * 100)))
  }

  return <div className="portfolio-chart">
    <div className="portfolio-chart-scale">
      <PortfolioAmount value={max}/>
      {max !== min && <PortfolioAmount value={min}/>}
    </div>
    <div className="portfolio-chart-plot" onPointerMove={inspect}
      onPointerLeave={() => setHoveredX(null)} onPointerCancel={() => setHoveredX(null)}>
      <svg viewBox="0 0 100 100" preserveAspectRatio="none" role="img" aria-label="Изменение оценочного капитала в долларах">
        {point && <line className="portfolio-chart-guide" x1={point.x} x2={point.x}
          y1="0" y2="100" vectorEffect="non-scaling-stroke"/>}
        {curve ? <path d={curve.path} vectorEffect="non-scaling-stroke"/>
          : <circle className="portfolio-chart-point" cx={lastPoint.x} cy={lastPoint.y} r=".6"/>}
      </svg>
      {point && <>
        <span className="portfolio-chart-marker" aria-hidden="true" style={{left: `${point.x}%`, top: `${point.y}%`}}/>
        <div className="portfolio-chart-tooltip" data-position={point.y < 40 ? 'bottom' : 'top'}
          style={{left: `clamp(min(4.5rem, 50%), ${point.x}%, max(50%, calc(100% - 4.5rem)))`}}>
          <time dateTime={new Date(time * 1000).toISOString()}>{formatTime(time)}</time>
          <strong>≈ <PortfolioAmount value={value}/></strong>
        </div>
      </>}
    </div>
    <div className="portfolio-chart-dates">
      <span>{formatTime(from)}</span>
      {until > from && <span>{formatTime(until)}</span>}
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
