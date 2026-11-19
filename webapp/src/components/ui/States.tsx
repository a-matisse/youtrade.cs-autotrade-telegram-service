import type { ReactNode } from 'react'
export function FullScreenLoading() { return <main className="auth-page auth-page-loading" role="status"><span className="auth-page-label">Y.CS</span><div className="auth-page-progress" aria-hidden="true"/><p>Проверяем вход…</p></main> }
export function LoadingState({ text = 'Загружаем данные…' }: { text?: string }) { return <div className="state"><div className="loader small" /><p>{text}</p></div> }
export function ErrorState({ message, retry }: { message: string; retry?: () => void }) { return <div className="state error"><h3>Не удалось загрузить данные</h3><p>{message}</p>{retry && <button onClick={retry}>Повторить</button>}</div> }
export function EmptyState({ children = 'Данных пока нет.' }: { children?: ReactNode }) { return <div className="state empty"><p>{children}</p></div> }
