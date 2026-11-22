import {useEffect, useState} from 'react'
import type {CSSProperties} from 'react'
import {landingApi} from '../../api/services'
import type {LandingOperation} from '../../api/types'
import {AuthOperationCard} from './AuthOperationCard'
import {operationFadeOutMs, useOperationRotation} from './useOperationRotation'
import './AuthOperationPreview.css'

const refreshIntervalMs = 30_000
const compactScreen = '(max-width: 800px)'

function OperationGrid({operations}: {operations: LandingOperation[]}) {
    const {slots, leavingSlot, shiftAt, inspect} = useOperationRotation(operations, compactScreen)
    const style = {'--operation-preview-exit-time': `${operationFadeOutMs}ms`} as CSSProperties

    return <div className="operation-preview-stage" style={style}>
        {slots.map((operation, index) => <div key={index} className="operation-preview-slot"
            style={{'--operation-preview-shift': shiftAt(index)} as CSSProperties}
            onPointerEnter={event => {
                if (event.pointerType !== 'touch') inspect('hovered', index)
            }}
            onPointerLeave={() => inspect('hovered', null)}
            onPointerCancel={() => inspect('hovered', null)}
            onFocus={event => {
                if ((event.target as HTMLElement).matches(':focus-visible')) inspect('focused', index)
            }}
            onBlur={() => inspect('focused', null)}>
            {operation && <AuthOperationCard key={operation.id} operation={operation} leaving={leavingSlot === index}/>}
        </div>)}
    </div>
}

export function AuthOperationPreview() {
    const [operations, setOperations] = useState<LandingOperation[]>([])
    const [status, setStatus] = useState<'loading' | 'ready' | 'unavailable'>('loading')

    useEffect(() => {
        const media = window.matchMedia(compactScreen)
        const controller = new AbortController()
        let pending = false
        const refresh = async () => {
            if (pending || document.hidden || media.matches) return
            pending = true
            setStatus('loading')
            try {
                // The browser handles ETag revalidation with its HTTP cache.
                const snapshot = await landingApi.operations(controller.signal)
                if (controller.signal.aborted) return
                setOperations(snapshot.operations)
                setStatus('ready')
            } catch {
                if (!controller.signal.aborted) setStatus('unavailable')
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

    return <aside className="operation-preview site-control-surface--dark" aria-labelledby="operation-preview-title">
        <div className="operation-preview-content">
            <h2 id="operation-preview-title">Мы работаем<br/><span className="operation-preview-title-accent">всегда</span></h2>
            {operations.length ? <OperationGrid operations={operations}/>
                : <div className="operation-preview-stage">
                    {status === 'loading' ? <div className="operation-preview-status" role="status" aria-label="Загружаем операции">
                        <div className="site-wait-line" aria-hidden="true"/>
                    </div> : <p className="operation-preview-status" role="status">
                        {status === 'unavailable' ? 'Операции временно недоступны.' : 'Операций пока нет.'}
                    </p>}
                </div>}
        </div>
    </aside>
}
