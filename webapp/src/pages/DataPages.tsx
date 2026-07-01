import {useCallback, type ReactNode} from 'react'
import {dataApi} from '../api/services'
import type {DefaultResponse, ParameterSet, PortfolioItem, WordItem} from '../api/types'
import {AppLayout} from '../components/layout/AppLayout'
import {EmptyState, ErrorState, LoadingState} from '../components/ui/States'
import {useApiData} from '../hooks/useApiData'

function unwrap<T>(value: DefaultResponse<T[]> | T[] | null): T[] {
    if (!value) return [];
    if (Array.isArray(value)) return value;
    return value.data || value.result || []
}

function DataView<T>({loading, error, retry, items, children}: {
    loading: boolean;
    error: string | null;
    retry: () => void;
    items: T[];
    children: (item: T, index: number) => ReactNode
}) {
    if (loading) return <LoadingState/>;
    if (error) return <ErrorState message={error} retry={retry}/>;
    if (!items.length) return <EmptyState/>;
    return <div className="table-list">{items.map(children)}</div>
}

export function AccountsPage() {
    const loader = useCallback(() => dataApi.accounts(), []);
    const q = useApiData(loader);
    const raw = q.data?.content || q.data?.data || q.data?.items || [];
    return <AppLayout title="Аккаунты" subtitle="Покупатели, продавцы и воркеры">
        <section className="panel">
            <div className="panel-heading"><h2>Подключённые аккаунты</h2><span className="badge">Только просмотр</span>
            </div>
            <DataView<unknown> loading={q.loading} error={q.error} retry={q.reload} items={raw}>{(item, i) => <article className="row-card" key={i}>
                <b>Аккаунт {i + 1}</b><code>{JSON.stringify(item)}</code></article>}</DataView></section>
    </AppLayout>
}

export function ParametersPage() {
    const loader = useCallback(() => dataApi.params(), []);
    const q = useApiData(loader);
    const items = unwrap<ParameterSet>(q.data);
    return <AppLayout title="Параметры" subtitle="Наборы настроек автоторговли">
        <section className="panel">
            <div className="panel-heading"><h2>Наборы параметров</h2><span className="badge">Только просмотр</span>
            </div>
            <DataView<ParameterSet> loading={q.loading} error={q.error} retry={q.reload} items={items}>{item => <article className="row-card" key={item.tdpId}>
                <div><b>{item.givenName || `Набор #${item.tdpId}`}</b><span>{item.source} → {item.destination}</span>
                </div>
                <strong>{item.balance ?? '—'}</strong></article>}</DataView></section>
    </AppLayout>
}

export function PortfolioPage() {
    const loader = useCallback(() => dataApi.inventory(), []);
    const q = useApiData(loader);
    const items = unwrap<PortfolioItem>(q.data);
    return <AppLayout title="Портфель" subtitle="Загруженный инвентарь и торговые позиции">
        <section className="panel">
            <div className="panel-heading"><h2>Инвентарь</h2><span className="badge">Только просмотр</span></div>
            <DataView<PortfolioItem> loading={q.loading} error={q.error} retry={q.reload} items={items}>{(item, i) => <article className="row-card"
                                                                  key={item.id ?? item.itemId ?? i}>
                <div>
                    <b>{item.name || item.title || `Позиция #${item.id ?? i + 1}`}</b><span>{item.status || 'Без статуса'}</span>
                </div>
                <strong>{item.price ?? item.sellPrice ?? item.buyPrice ?? '—'}</strong></article>}</DataView></section>
    </AppLayout>
}

function WordColumn({title, loader}: {
    title: string;
    loader: () => Promise<DefaultResponse<WordItem[]> | WordItem[]>
}) {
    const q = useApiData(loader);
    const items = unwrap<WordItem>(q.data);
    return <section className="panel">
        <div className="panel-heading"><h2>{title}</h2></div>
        <DataView<WordItem> loading={q.loading} error={q.error} retry={q.reload} items={items}>{item => <article className="row-card" key={item.id}>
            <b>{item.word || item.value || `Запись #${item.id}`}</b></article>}</DataView></section>
}

export function DictionariesPage() {
    const included = useCallback(() => dataApi.includedWords(), []);
    const excluded = useCallback(() => dataApi.excludedWords(), []);
    return <AppLayout title="Словари" subtitle="Фильтрация предметов при автопокупке">
        <div className="two-columns"><WordColumn title="Обязательные слова" loader={included}/><WordColumn
            title="Исключённые слова" loader={excluded}/></div>
    </AppLayout>
}
