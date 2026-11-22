import {useEffect, useState} from 'react'
import type {LandingOperation} from '../../api/types'
import {useNotifications} from '../ui/notifications'
import icons from '../../../../shared/site/icons.svg?no-inline'

const copiedDurationMs = 1_500
const amountFormat = new Intl.NumberFormat('ru-RU', {minimumFractionDigits: 2, maximumFractionDigits: 2})

function OperationImage({url}: {url: string | null}) {
    const [failed, setFailed] = useState(false)
    return <div className="operation-preview-image" aria-hidden="true">
        {url && !failed
            ? <img src={url} alt="" decoding="async" referrerPolicy="no-referrer" onError={() => setFailed(true)}/>
            : <svg><use href={`${icons}#image`}/></svg>}
    </div>
}

export function AuthOperationCard({operation, leaving}: {operation: LandingOperation; leaving: boolean}) {
    const [copiedAt, setCopiedAt] = useState<number>()
    const notifications = useNotifications()

    useEffect(() => {
        if (copiedAt === undefined) return
        const timer = window.setTimeout(() => setCopiedAt(undefined), copiedDurationMs)
        return () => window.clearTimeout(timer)
    }, [copiedAt])

    const copyName = async () => {
        try {
            await navigator.clipboard.writeText(operation.itemName)
            setCopiedAt(performance.now())
        } catch {
            notifications.error('Попробуйте ещё раз.', 'Не удалось скопировать название')
        }
    }

    return <div className="operation-preview-card" data-operation-id={operation.id} data-leaving={leaving}>
        <div className="operation-preview-visual">
            <OperationImage key={operation.imageUrl} url={operation.imageUrl}/>
            <div className="operation-preview-name">
                <button className="site-control site-control--quiet site-control--large operation-preview-copy"
                    type="button" aria-label={`Скопировать название: ${operation.itemName}`} onClick={() => void copyName()}>
                    <span>{operation.itemName}</span>
                    <svg aria-hidden="true"><use href={`${icons}#copy`}/></svg>
                </button>
            </div>
        </div>
        <div className="operation-preview-details">
            <span className="operation-preview-direction" data-direction={operation.type}>
                {operation.type === 'BUY' ? 'Покупка' : 'Продажа'}
            </span>
            <span className="operation-preview-amount">
                {amountFormat.format(operation.amount)}&nbsp;<span className="operation-preview-currency">{operation.currency}</span>
            </span>
        </div>
        <div className="operation-preview-feedback" role="status">
            {copiedAt !== undefined && <span className="operation-preview-copied" key={copiedAt}>
                <svg aria-hidden="true"><use href={`${icons}#bolt`}/></svg>
                Скопировано
                <svg aria-hidden="true"><use href={`${icons}#bolt`}/></svg>
            </span>}
        </div>
    </div>
}
