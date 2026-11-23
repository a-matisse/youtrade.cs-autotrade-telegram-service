import {createContext, useContext} from 'react'

export type Notifications = {error(message: string, title?: string): void; dismiss(): void}
export const NotificationsContext = createContext<Notifications | null>(null)

export function useNotifications() {
    const notifications = useContext(NotificationsContext)
    if (!notifications) throw new Error('useNotifications должен использоваться внутри SiteNotifications')
    return notifications
}
