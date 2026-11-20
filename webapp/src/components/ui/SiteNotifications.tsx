import {useCallback, useMemo, useRef, useState, type ReactNode} from 'react'
import {NotificationsContext} from './notifications'
import {Toast} from './Toast'

type Notification = {id: number; title: string; message: string}

export function SiteNotifications({children}: {children: ReactNode}) {
    const [notification, setNotification] = useState<Notification | null>(null)
    const sequence = useRef(0)
    const error = useCallback((message: string, title = 'Не удалось выполнить действие') => {
        setNotification({id: ++sequence.current, title, message})
    }, [])
    const notifications = useMemo(() => ({error}), [error])
    return <NotificationsContext.Provider value={notifications}>
        {children}
        {notification && <Toast key={notification.id} title={notification.title} message={notification.message} onClose={() => setNotification(null)}/>}
    </NotificationsContext.Provider>
}
