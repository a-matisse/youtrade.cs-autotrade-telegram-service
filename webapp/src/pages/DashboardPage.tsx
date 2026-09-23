import {useCallback} from 'react'
import {Link} from 'react-router-dom'
import {dataApi} from '../api/services'
import {AppLayout} from '../components/layout/AppLayout'
import {ErrorState, LoadingState} from '../components/ui/States'
import {useApiData} from '../hooks/useApiData'
import './DashboardPage.css'

export function DashboardPage() {
    const loader = useCallback(() => dataApi.accountInfo(), []);
    const {data, loading, error, reload} = useApiData(loader)
    const blockedUntil = data?.blockedUntil ? new Date(`${data.blockedUntil}Z`) : null
    const blocked = Boolean(blockedUntil && !Number.isNaN(blockedUntil.getTime()) && blockedUntil.getTime() > Date.now())
    return <AppLayout title="Обзор" subtitle="Состояние торгового кабинета" blocked={blocked}>{loading ? <LoadingState/> : error ?
        <ErrorState message={error} retry={reload}/> : <>
            {blocked && <section className="panel account-restriction"><h2>Обслуживание приостановлено</h2><p>До {blockedUntil!.toLocaleString('ru-RU', {timeZone: 'UTC'})} UTC. Баланс остаётся доступен для просмотра. По вопросам ограничения и средств <a href="https://t.me/youtradecs_sup">напишите в поддержку</a>.</p></section>}
            <section className="stats">
                <article><span>Баланс</span><strong>{data?.balance ?? '—'}</strong></article>
                {blocked && <article><span>Реферальный баланс</span><strong>{data?.referralBalance ?? '—'}</strong></article>}
                <article><span>ID профиля</span><strong>{data?.tdpId ?? data?.tdId ?? '—'}</strong></article>
                <article><span>Статус</span><strong>{blocked ? 'Приостановлен' : data?.qualified === false ? 'Ограничен' : 'Активен'}</strong>
                </article>
            </section>
            {!blocked && <section className="panel">
                <div className="panel-heading">
                    <div><h2>Быстрый доступ</h2><p>Основные сценарии Telegram-клиента в web-формате.</p></div>
                </div>
                <div className="quick-grid"><Link
                    to="/accounts"><b>Аккаунты</b><span>Просмотр подключений →</span></Link><Link
                    to="/portfolio"><b>Портфель</b><span>Инвентарь и позиции →</span></Link><Link
                    to="/parameters"><b>Параметры</b><span>Наборы автоторговли →</span></Link><Link
                    to="/dictionaries"><b>Словари</b><span>Фильтры названий →</span></Link></div>
            </section>}
        </>}</AppLayout>
}
