import {useEffect, useReducer} from 'react'
import type {LandingOperation} from '../../api/types'

const slotCount = 9
const rotationIntervalMs = 1_500
const fadeOutMs = 300

type Slots = (LandingOperation | null)[]
type Rotation = {from: number; to: number; operation: LandingOperation}
type Interaction = 'hovered' | 'focused'
type Grid = {
    slots: Slots
    hovered: number | null
    focused: number | null
    lastChanged: number | null
    rotation: Rotation | null
}
type Action =
    | {type: 'refresh'; operations: LandingOperation[]}
    | {type: 'inspect'; interaction: Interaction; slot: number | null; operations: LandingOperation[]}
    | {type: 'rotate'; operations: LandingOperation[]; randomSlot: number; randomOperation: number}
    | {type: 'finish'; rotation: Rotation}
    | {type: 'cancel'}

function createGrid(operations: LandingOperation[]): Grid {
    const pool = [...operations]
    const emptySlot = Math.floor(Math.random() * slotCount)
    const slots = Array.from({length: slotCount}, (_, index) => index === emptySlot ? null
        : pool.splice(Math.floor(Math.random() * pool.length), 1)[0] ?? null)
    return {slots, hovered: null, focused: null, lastChanged: null, rotation: null}
}

function isInspected(grid: Grid, index: number) {
    return index === grid.hovered || index === grid.focused
}

function refreshSlots(grid: Grid, operations: LandingOperation[]) {
    const byId = new Map(operations.map(operation => [operation.id, operation]))
    const slots = grid.slots.map((operation, index) => operation
        ? byId.get(operation.id) ?? (isInspected(grid, index) ? operation : null) : null)
    const visibleIds = new Set(slots.map(operation => operation?.id))
    const available = operations.filter(operation => !visibleIds.has(operation.id))
    const vacancies = slots.filter(slot => slot === null).length - 1
    let filled = 0
    return slots.map(slot => {
        if (slot || filled >= vacancies) return slot
        return available[filled++] ?? null
    })
}

function reduceGrid(grid: Grid, action: Action): Grid {
    switch (action.type) {
        case 'refresh':
            return {...grid, slots: refreshSlots(grid, action.operations), rotation: null}
        case 'inspect': {
            const next = {...grid, [action.interaction]: action.slot}
            const previousSlot = grid[action.interaction]
            if (previousSlot !== null && !isInspected(next, previousSlot)) {
                const previous = grid.slots[previousSlot]
                if (previous && !action.operations.some(operation => operation.id === previous.id))
                    next.slots = refreshSlots(next, action.operations)
            }
            if (next.rotation?.from === action.slot || next.slots !== grid.slots) next.rotation = null
            return next
        }
        case 'rotate': {
            if (grid.rotation || action.operations.length < 2) return grid
            const candidates = grid.slots.flatMap((slot, index) => slot && !isInspected(grid, index)
                && index !== grid.lastChanged ? [index] : [])
            const from = candidates[Math.floor(action.randomSlot * candidates.length)]
            const to = grid.slots.indexOf(null)
            if (from === undefined || to === -1) return grid
            const visibleIds = new Set(grid.slots.map(operation => operation?.id))
            const available = action.operations.filter(operation => !visibleIds.has(operation.id))
            const operation = available[Math.floor(action.randomOperation * available.length)] ?? grid.slots[from]
            if (!operation) return grid
            return {...grid, rotation: {from, to, operation}}
        }
        case 'finish': {
            if (grid.rotation !== action.rotation) return grid
            const {from, to, operation} = action.rotation
            const slots = [...grid.slots]
            slots[from] = null
            slots[to] = operation
            return {...grid, slots, rotation: null, lastChanged: to}
        }
        case 'cancel':
            return grid.rotation ? {...grid, rotation: null} : grid
    }
}

export function useOperationRotation(operations: LandingOperation[], compactScreen: string) {
    const [grid, dispatch] = useReducer(reduceGrid, operations, createGrid)
    const {rotation} = grid

    useEffect(() => dispatch({type: 'refresh', operations}), [operations])

    useEffect(() => {
        const motion = window.matchMedia(`${compactScreen}, (prefers-reduced-motion: reduce)`)
        let timer: number | undefined
        const synchronize = () => {
            window.clearInterval(timer)
            if (document.hidden || motion.matches) {
                dispatch({type: 'cancel'})
                return
            }
            timer = window.setInterval(() => dispatch({type: 'rotate', operations,
                randomSlot: Math.random(), randomOperation: Math.random()}), rotationIntervalMs)
        }
        synchronize()
        document.addEventListener('visibilitychange', synchronize)
        motion.addEventListener('change', synchronize)
        return () => {
            window.clearInterval(timer)
            document.removeEventListener('visibilitychange', synchronize)
            motion.removeEventListener('change', synchronize)
        }
    }, [compactScreen, operations])

    useEffect(() => {
        if (!rotation) return
        const timer = window.setTimeout(() => dispatch({type: 'finish', rotation}), fadeOutMs)
        return () => window.clearTimeout(timer)
    }, [rotation])

    return {slots: grid.slots, leavingSlot: rotation?.from,
        inspect: (interaction: Interaction, slot: number | null) => dispatch({type: 'inspect', interaction, slot, operations})}
}
