import {useEffect, useRef, useState} from 'react'
import {landingApi} from '../../api/services'
import type {LandingOperation} from '../../api/types'
import icons from '../../../../shared/site/icons.svg?no-inline'
import './AuthOperationPreview.css'

const refreshIntervalMs = 30_000
const rotationIntervalMs = 7_000
const transitionMs = 700
const compactScreen = '(max-width: 800px)'
const amountFormat = new Intl.NumberFormat('ru-RU', {minimumFractionDigits: 2, maximumFractionDigits: 2})

function chooseOperation(operations: LandingOperation[], currentId?: string) {
    const candidates = operations.filter(operation => operation.id !== currentId)
    return candidates[Math.floor(Math.random() * candidates.length)]
}

function OperationImage({url}: {url: string | null}) {
    const [failed, setFailed] = useState(false)
    return <div className="operation-preview-image" aria-hidden="true">
        {url && !failed
            ? <img src={url} alt="" decoding="async" referrerPolicy="no-referrer" onError={() => setFailed(true)}/>
            : <svg><use href={`${icons}#image`}/></svg>}
    </div>
}

function OperationCarousel({operations}: {operations: LandingOperation[]}) {
    const [currentId, setCurrentId] = useState(() => chooseOperation(operations).id)
    const [transition, setTransition] = useState<{from: string; to: string} | null>(null)
    const [hovered, setHovered] = useState(false)
    const [focused, setFocused] = useState(false)
    const [canAnimate, setCanAnimate] = useState(false)
    const timer = useRef<number>()
    const latestOperations = useRef(operations)
    const current = operations.find(operation => operation.id === currentId) ?? operations[0]
    const incoming = transition?.from === current.id ? operations.find(operation => operation.id === transition.to) : undefined
    const inspecting = hovered || focused
    const shouldRotate = canAnimate && !inspecting && operations.length > 1

    useEffect(() => { latestOperations.current = operations }, [operations])

    useEffect(() => {
        const motion = window.matchMedia(`${compactScreen}, (prefers-reduced-motion: reduce)`)
        const synchronize = () => {
            const enabled = !document.hidden && !motion.matches
            setCanAnimate(enabled)
            if (!enabled) {
                window.clearTimeout(timer.current)
                setTransition(null)
            }
        }
        synchronize()
        document.addEventListener('visibilitychange', synchronize)
        motion.addEventListener('change', synchronize)
        return () => {
            document.removeEventListener('visibilitychange', synchronize)
            motion.removeEventListener('change', synchronize)
        }
    }, [])

    useEffect(() => {
        if (!shouldRotate) {
            setTransition(null)
            return
        }
        const schedule = () => {
            timer.current = window.setTimeout(() => {
                const next = chooseOperation(latestOperations.current, current.id)
                if (!next) return
                setTransition({from: current.id, to: next.id})
                timer.current = window.setTimeout(() => {
                    setTransition(null)
                    if (latestOperations.current.some(operation => operation.id === next.id)) setCurrentId(next.id)
                    else schedule()
                }, transitionMs)
            }, rotationIntervalMs)
        }
        schedule()
        return () => window.clearTimeout(timer.current)
    }, [current.id, shouldRotate])

    const stopTransition = () => {
        window.clearTimeout(timer.current)
        setTransition(null)
    }

    return <div className="operation-preview-stage" role="group" tabIndex={0} aria-label={current.itemName}
        data-inspecting={inspecting}
        onPointerEnter={event => {
            if (event.pointerType === 'touch') return
            stopTransition()
            setHovered(true)
        }}
        onPointerLeave={() => setHovered(false)}
        onFocus={() => { stopTransition(); setFocused(true) }}
        onBlur={() => setFocused(false)}>
        {(incoming ? [current, incoming] : [current]).map(operation => <div key={operation.id}
            className="operation-preview-card" data-operation-id={operation.id}
            data-phase={incoming ? (operation.id === incoming.id ? 'incoming' : 'outgoing') : 'current'}
            aria-hidden={operation.id === incoming?.id ? true : undefined}>
            <div className="operation-preview-visual">
                <OperationImage key={operation.imageUrl} url={operation.imageUrl}/>
                <span className="operation-preview-name" aria-hidden="true">{operation.itemName}</span>
            </div>
            <div className="operation-preview-details">
                <span className="operation-preview-direction" data-direction={operation.type}>
                    {operation.type === 'BUY' ? 'Покупка' : 'Продажа'}
                </span>
                <span className="operation-preview-amount">
                    {amountFormat.format(operation.amount)}&nbsp;<span className="operation-preview-currency">{operation.currency}</span>
                </span>
            </div>
        </div>)}
    </div>
}

export function AuthOperationPreview() {
    const [operations, setOperations] = useState<LandingOperation[] | null>(null)
    const [unavailable, setUnavailable] = useState(false)

    useEffect(() => {
        const media = window.matchMedia(compactScreen)
        const controller = new AbortController()
        let pending = false
        const refresh = async () => {
            if (pending || document.hidden || media.matches) return
            pending = true
            try {
                // The browser handles ETag revalidation with its HTTP cache.
                const snapshot = await landingApi.operations(controller.signal)
                if (controller.signal.aborted) return
                setOperations(snapshot.operations)
                setUnavailable(false)
            } catch {
                if (!controller.signal.aborted) setUnavailable(true)
            } finally {
                pending = false
            }
        }
        void refresh()
        const interval = window.setInterval(refresh, refreshIntervalMs)
        document.addEventListener('visibilitychange', refresh)
        media.addEventListener('change', refresh)
        return () => {
            controller.abort()
            window.clearInterval(interval)
            document.removeEventListener('visibilitychange', refresh)
            media.removeEventListener('change', refresh)
        }
    }, [])

    return <aside className="operation-preview" aria-labelledby="operation-preview-title">
        <div className="operation-preview-content">
            <h2 id="operation-preview-title">Мы работаем<br/><span className="operation-preview-title-accent">всегда</span></h2>
            {operations?.length ? <OperationCarousel operations={operations}/>
                : <p className="operation-preview-status" role="status">
                    {unavailable ? 'Операции временно недоступны.' : operations ? 'Операций пока нет.' : 'Загружаем операции…'}
                </p>}
        </div>
    </aside>
}
