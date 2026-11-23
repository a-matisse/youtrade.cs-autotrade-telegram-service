import {useState} from 'react'
import type {PortfolioBreakdown, PortfolioCapital, PortfolioSummary} from '../../api/portfolioTypes'
import {PortfolioAmount} from './PortfolioAmount'
import {PortfolioMetrics} from './PortfolioSection'
import icons from '../../../../shared/site/icons.svg?no-inline'

type MarketBalance = PortfolioBreakdown['marketBalances'][number]

function saleBalances(portfolios: PortfolioBreakdown[]) {
  const balancesByAccount = new Map<string, MarketBalance>()
  for (const {marketBalances} of portfolios) {
    for (const balance of marketBalances) {
      if (balance.side !== 'SELL') continue
      const key = `${balance.market}:${balance.accountId}`
      const previous = balancesByAccount.get(key)
      if (!previous || (balance.observedAt ?? 0) > (previous.observedAt ?? 0))
        balancesByAccount.set(key, balance)
    }
  }
  const balances = [...balancesByAccount.values()]
  const total = (field: 'availableUsd' | 'pendingUsd') => {
    if (!balances.length || balances.some(balance => balance[field] === null)) return null
    return balances.reduce((sum, balance) => sum + (balance[field] ?? 0), 0)
  }
  return {available: total('availableUsd'), pending: total('pendingUsd')}
}

function BalancePair({title, available, pending}: {title: string; available: number | null; pending: number | null}) {
  return <div className="portfolio-balance-row">
    <h3>{title}</h3>
    <dl className="portfolio-balance-pair">
      <div>
        <dt className="portfolio-visually-hidden">Доступно</dt>
        <dd><svg aria-hidden="true"><use href={`${icons}#flame`}/></svg><PortfolioAmount value={available}/></dd>
      </div>
      <div className="portfolio-pending-balance">
        <dt className="portfolio-visually-hidden">В ожидании</dt>
        <dd><svg aria-hidden="true"><use href={`${icons}#snowflake`}/></svg><PortfolioAmount value={pending}/></dd>
      </div>
    </dl>
  </div>
}

export function PortfolioCapitalDetails({summary}: {summary: PortfolioSummary}) {
  const {capital, portfolios} = summary
  const sale = saleBalances(portfolios)

  return <>
    <div className="portfolio-balance-rows">
      <BalancePair title="Покупка" available={capital.buyAvailableUsd} pending={capital.buyPendingUsd}/>
      <BalancePair title="Продажа" available={sale.available} pending={sale.pending}/>
    </div>
    <section className="portfolio-valuation">
      <div className="portfolio-valuation-header">
        <h3>Оценка предметов{' '}
          <span className="portfolio-caption-count"
            aria-label={`Оценено ${capital.valuedPositionCount} из ${capital.positionCount}`}>
            {capital.valuedPositionCount}/{capital.positionCount}
          </span>
        </h3>
        <strong><PortfolioAmount value={capital.inventoryEstimatedUsd}/></strong>
      </div>
      <PortfolioMetrics values={[
        ['Себестоимость', <PortfolioAmount value={capital.matchedOpenCostUsd}/>],
        ['Ожидаемая прибыль', <PortfolioAmount value={capital.estimatedPnlUsd} signed/>],
        ['Доходность к себестоимости', <PortfolioAmount value={capital.estimatedReturnPercent} percent signed/>],
      ]}/>
    </section>
    {capital.partial && <p className="portfolio-note">Часть данных отсутствует или устарела</p>}
  </>
}

export function PortfolioDistribution({capital}: {capital: PortfolioCapital}) {
  const [selectedKind, setSelectedKind] = useState<string | null>(null)
  const {buyAvailableUsd, buyPendingUsd, saleCashEstimatedUsd, inventoryEstimatedUsd} = capital
  const buy = buyAvailableUsd === null || buyPendingUsd === null ? null : buyAvailableUsd + buyPendingUsd
  const buckets = [
    {kind: 'buy', label: 'Покупка', value: buy},
    {kind: 'sale', label: 'Продажа', value: saleCashEstimatedUsd},
    {kind: 'items', label: 'Предметы', value: inventoryEstimatedUsd},
  ]
  const total = buckets.reduce((sum, bucket) => sum + (bucket.value ?? 0), 0)
  let offset = 0
  const segments = total > 0 ? buckets.filter(bucket => (bucket.value ?? 0) > 0).map(bucket => {
    const percent = (bucket.value ?? 0) * 100 / total
    const segment = {...bucket, percent, offset}
    offset += percent
    return segment
  }) : []
  const selected = segments.find(segment => segment.kind === selectedKind)
  const active = selected ?? segments.reduce<(typeof segments)[number] | undefined>((largest, segment) =>
    !largest || segment.percent > largest.percent ? segment : largest, undefined)

  return <div className="portfolio-distribution">
    <div className="portfolio-distribution-ring">
      <svg viewBox="0 0 100 100" role="group" aria-label="Доли капитала">
        <circle className="portfolio-distribution-track" cx="50" cy="50" r="38"/>
        {segments.map(({kind, label, percent, offset: start}) => <circle
          className="portfolio-distribution-segment" key={kind} data-kind={kind}
          cx="50" cy="50" r="38" pathLength="100" transform="rotate(-90 50 50)"
          strokeDasharray={`${percent} ${100 - percent}`} strokeDashoffset={-start}
          tabIndex={0} aria-label={label}
          onPointerEnter={() => setSelectedKind(kind)} onFocus={() => setSelectedKind(kind)}
        />)}
      </svg>
      {active && <output className="portfolio-distribution-percent" data-kind={active.kind}>
        <PortfolioAmount key={active.kind} value={active.percent} percent/>
      </output>}
    </div>
    <dl className="portfolio-distribution-legend">
      {buckets.map(({kind, label, value}) => <div key={kind} data-kind={kind}>
        <dt>{label}</dt><dd><PortfolioAmount value={value}/></dd>
      </div>)}
    </dl>
  </div>
}
