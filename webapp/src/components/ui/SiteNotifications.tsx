import {useCallback, useLayoutEffect, useMemo, useRef, useState, type ReactNode} from 'react'
import {useLocation} from 'react-router-dom'
import {NotificationsContext} from './notifications'
import {Toast} from './Toast'

export function SiteNotifications({children}: {children: ReactNode}) {
    const location = useLocation()
    const currentLocation = useRef(location)
    const [notification, setNotification] = useState<{id: number; title: string; message: string; location: typeof location} | null>(null)
    const sequence = useRef(0)

    useLayoutEffect(() => { currentLocation.current = location }, [location])

    const error = useCallback((message: string, title = 'Не удалось выполнить действие') => {
        if (currentLocation.current !== location) return
        setNotification({id: ++sequence.current, title, message, location})
    }, [location])
    const dismiss = useCallback(() => {
        setNotification(null)
    }, [])
    const notifications = useMemo(() => ({error, dismiss}), [error, dismiss])
    return <NotificationsContext.Provider value={notifications}>
        {children}
        {notification?.location === location && <Toast key={notification.id} title={notification.title} message={notification.message} onClose={dismiss}/>}
    </NotificationsContext.Provider>
}
