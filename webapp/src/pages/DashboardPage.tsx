import {useCallback} from 'react'
import {Link} from 'react-router-dom'
import {dataApi} from '../api/services'
import {AppLayout} from '../components/layout/AppLayout'
import {ErrorState, LoadingState} from '../components/ui/States'
import {useApiData} from '../hooks/useApiData'

export function DashboardPage() {
    const loader = useCallback(() => dataApi.accountInfo(), []);
    const {data, loading, error, reload} = useApiData(loader)
    return <AppLayout title="Обзор" subtitle="Состояние торгового кабинета">{loading ? <LoadingState/> : error ?
        <ErrorState message={error} retry={reload}/> : <>
            <section className="stats">
                <article><span>Баланс</span><strong>{data?.balance ?? '—'}</strong></article>
                <article><span>ID профиля</span><strong>{data?.tdpId ?? data?.tdId ?? '—'}</strong></article>
                <article><span>Статус</span><strong>{data?.qualified === false ? 'Ограничен' : 'Активен'}</strong>
                </article>
            </section>
            <section className="panel">
                <div className="panel-heading">
                    <div><h2>Быстрый доступ</h2><p>Основные сценарии Telegram-клиента в web-формате.</p></div>
                </div>
                <div className="quick-grid"><Link
                    to="/accounts"><b>Аккаунты</b><span>Просмотр подключений →</span></Link><Link
                    to="/portfolio"><b>Портфель</b><span>Инвентарь и позиции →</span></Link><Link
                    to="/parameters"><b>Параметры</b><span>Наборы автоторговли →</span></Link><Link
                    to="/dictionaries"><b>Словари</b><span>Фильтры названий →</span></Link></div>
            </section>
        </>}</AppLayout>
}
