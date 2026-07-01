import type { ReactNode } from 'react'
export function FullScreenLoading() { return <main className="center-screen"><div className="loader" aria-label="Загрузка" /><p>Проверяем сессию…</p></main> }
export function LoadingState({ text = 'Загружаем данные…' }: { text?: string }) { return <div className="state"><div className="loader small" /><p>{text}</p></div> }
export function ErrorState({ message, retry }: { message: string; retry?: () => void }) { return <div className="state error"><h3>Не удалось загрузить данные</h3><p>{message}</p>{retry && <button onClick={retry}>Повторить</button>}</div> }
export function EmptyState({ children = 'Данных пока нет.' }: { children?: ReactNode }) { return <div className="state empty"><p>{children}</p></div> }
