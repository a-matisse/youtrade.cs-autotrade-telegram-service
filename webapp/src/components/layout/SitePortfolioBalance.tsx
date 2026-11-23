import {useEffect} from 'react'
import {portfolioApi} from '../../api/portfolio'
import {PortfolioAmount} from '../portfolio/PortfolioAmount'
import {usePortfolioPrivacy} from '../portfolio/portfolioPrivacy'
import {usePortfolioData} from '../portfolio/usePortfolioData'
import {useNotifications} from '../ui/notifications'
import icons from '../../../../shared/site/icons.svg?no-inline'

export function SitePortfolioBalance() {
    const {hidden, toggle} = usePortfolioPrivacy()
    const {data, error, loading, reload} = usePortfolioData(portfolioApi.wallet)
    const {error: notifyError} = useNotifications()
    useEffect(() => {
        if (error) notifyError(error, 'Не удалось загрузить сервисный баланс')
    }, [error, notifyError])

    return <>
        <button className="site-control site-control--quiet site-control--icon" type="button"
            aria-label="Скрывать суммы" aria-pressed={hidden}
            title={hidden ? 'Показать суммы' : 'Скрыть суммы'} onClick={toggle}>
            <svg aria-hidden="true"><use href={`${icons}#${hidden ? 'eye-off' : 'eye'}`}/></svg>
        </button>
        {error ? <button className="site-control site-control--quiet site-wallet-retry" type="button"
            aria-label="Загрузить сервисный баланс ещё раз" onClick={reload}>—</button>
            : <span className="site-wallet-balance" role="group" aria-label="Сервисный баланс" aria-busy={loading}>
                <PortfolioAmount value={data?.serviceBalanceUsd ?? null}/>
            </span>}
    </>
}
