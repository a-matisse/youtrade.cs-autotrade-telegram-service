import {useCallback, useEffect, useState} from 'react'
import {portfolioApi} from '../../api/portfolio'
import type {ListingStatus} from '../../api/portfolioTypes'
import {useNotifications} from '../ui/notifications'
import {formatTime, marketName} from './format'
import {PortfolioAmount} from './PortfolioAmount'
import {PortfolioFilter} from './PortfolioFilter'
import {PortfolioSection} from './PortfolioSection'
import {usePortfolioData} from './usePortfolioData'

const desktopQuery = '(min-width: 961px)'
const listings: Record<ListingStatus, string> = {
  NOT_LISTED: 'Не выставлен',
  TO_BE_LISTED: 'Готовится к продаже',
  LISTED: 'Выставлен',
  DELISTED: 'Снят с продажи',
}

export function PortfolioPositions({tdpId}: {tdpId: number | null}) {
  const [desktop, setDesktop] = useState(false)
  useEffect(() => {
    const media = window.matchMedia(desktopQuery)
    const update = () => setDesktop(media.matches)
    update()
    media.addEventListener('change', update)
    return () => media.removeEventListener('change', update)
  }, [])
  return desktop ? <PositionsTable tdpId={tdpId}/> : null
}

function PositionsTable({tdpId}: {tdpId: number | null}) {
  const {dismiss} = useNotifications()
  const [status, setStatus] = useState<ListingStatus | ''>('')
  const [cursors, setCursors] = useState([0])
  const afterId = cursors[cursors.length - 1]
  const load = useCallback((signal: AbortSignal) => portfolioApi.positions(tdpId, status, afterId, signal),
    [tdpId, status, afterId])
  const query = usePortfolioData(load)
  const nextAfterId = query.data?.nextAfterId ?? null

  return <PortfolioSection title="Открытые позиции" {...query} className="portfolio-wide"
    actions={<PortfolioFilter label="Статус" value={status} onChange={event => {
      dismiss()
      setStatus(event.target.value as ListingStatus | '')
      setCursors([0])
    }}>
      <option value="">Все статусы</option>
      {Object.entries(listings).map(([value, label]) => <option value={value} key={value}>{label}</option>)}
    </PortfolioFilter>}>
    {query.data?.positions.length ? <div className="portfolio-table-scroll">
      <table className="portfolio-table" aria-label="Открытые позиции">
        <thead><tr>
          <th scope="col">Предмет</th><th scope="col">Статус</th><th scope="col">Покупка</th>
          <th scope="col">Себестоимость</th><th scope="col">К получению</th><th scope="col">Ожидаемый результат</th>
        </tr></thead>
        <tbody>{query.data.positions.map(position => <tr key={position.positionId}>
          <th scope="row">{position.itemName}<small>{marketName(position.boughtOn)} → {marketName(position.destination)}</small></th>
          <td>{listings[position.listingStatus]}{position.held && <small>Удержание до {formatTime(position.unlockAt)}</small>}</td>
          <td>{formatTime(position.purchasedAt)}</td>
          <td><PortfolioAmount value={position.purchaseCostUsd}/></td>
          <td><PortfolioAmount value={position.estimatedNetProceedsUsd}/></td>
          <td><PortfolioAmount value={position.estimatedPnlUsd} signed/>
            <small><PortfolioAmount value={position.estimatedReturnPercent} percent signed/></small>
          </td>
        </tr>)}</tbody>
      </table>
    </div> : <p>Открытых позиций пока нет</p>}
    {(cursors.length > 1 || nextAfterId !== null) && <div className="portfolio-pagination">
      <button type="button" className="site-control site-control--quiet" disabled={cursors.length === 1}
        onClick={() => setCursors(previous => previous.slice(0, -1))}>Назад</button>
      <button type="button" className="site-control site-control--quiet" disabled={nextAfterId === null}
        onClick={() => { if (nextAfterId !== null) setCursors(previous => [...previous, nextAfterId]) }}>Далее</button>
    </div>}
  </PortfolioSection>
}
