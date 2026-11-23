import {formatPercent, formatUsd} from './format'
import {usePortfolioPrivacy} from './portfolioPrivacy'

export function PortfolioAmount({value, percent = false, signed = false}: {
  value: number | null
  percent?: boolean
  signed?: boolean
}) {
  const {hidden} = usePortfolioPrivacy()
  if (hidden && !percent) return <span className="portfolio-amount" aria-label="Скрыто">****</span>
  if (value === null) return <span className="portfolio-amount">—</span>

  const sign = signed && Math.abs(value) >= .005 ? (value > 0 ? 'positive' : 'negative') : undefined
  return <span className="portfolio-amount" data-sign={sign}>
    {percent ? formatPercent(value, signed) : formatUsd(value, signed)}
  </span>
}
