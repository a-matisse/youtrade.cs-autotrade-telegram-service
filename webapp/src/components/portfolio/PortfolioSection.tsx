import {useEffect, type ReactNode} from 'react'
import {useNotifications} from '../ui/notifications'

export function PortfolioSection({title, count, loading, error, reload, children, actions, className = ''}: {
  title: string
  count?: number
  loading: boolean
  error: string | null
  reload: () => void
  children: ReactNode
  actions?: ReactNode
  className?: string
}) {
  const {error: notifyError} = useNotifications()
  useEffect(() => {
    if (error) notifyError(error, `Не удалось загрузить данные: ${title.toLocaleLowerCase('ru')}`)
  }, [error, title, notifyError])

  return <section className={`portfolio-section ${className}`}>
    <div className="portfolio-section-header">
      <h2>{title}{count !== undefined && <> <span className="portfolio-caption-count">{count}</span></>}</h2>
      {actions}
    </div>
    {loading ? <div className="portfolio-loading" role="status" aria-label="Загружаем данные">
      <div className="site-wait-line" aria-hidden="true"/>
    </div> : error ? <div className="portfolio-unavailable">
      <p>Данные недоступны</p>
      <button className="site-control site-control--quiet" type="button" onClick={reload}>Повторить</button>
    </div> : children}
  </section>
}

export function PortfolioMetrics({values}: {values: [label: string, value: ReactNode][]}) {
  return <dl className="portfolio-metrics">
    {values.map(([label, value]) => <div key={label}><dt>{label}</dt><dd>{value}</dd></div>)}
  </dl>
}
