import {useEffect, useReducer, useRef} from 'react'
import type {LandingOperation} from '../../api/types'

const columns = 3
const slotCount = columns * columns
const maxCards = slotCount - 1
const fillIntervalMs = 130
const rotationIntervalMs = 1_250
const repeatDelayMs = 4_500
const waveOffsetPx = 7
export const operationFadeOutMs = 550

type Slots = (LandingOperation | null)[]
type Move = {from: number; to: number}
type Rotation = Move & {incoming: LandingOperation; outgoingName: string}
type RecentItem = {name: string; until: number}
type Interaction = 'hovered' | 'focused'
type Grid = {
    slots: Slots
    hovered: number | null
    focused: number | null
    lastMove: Move | null
    rotation: Rotation | null
    recentItems: RecentItem[]
}
type Action =
    | {type: 'refresh'; operations: LandingOperation[]}
    | {type: 'inspect'; interaction: Interaction; slot: number | null; operations: LandingOperation[]}
    | {type: 'fill'; operations: LandingOperation[]; randomSlot: number; randomOperation: number}
    | {type: 'rotate'; operations: LandingOperation[]; randomSlot: number; randomOperation: number; now: number}
    | {type: 'finish'; rotation: Rotation; now: number}
    | {type: 'cancel'}

function distinctOperations(operations: LandingOperation[], names: Set<string>) {
    const available: LandingOperation[] = []
    for (const operation of operations) {
        if (names.has(operation.itemName)) continue
        names.add(operation.itemName)
        available.push(operation)
    }
    return available
}

function createGrid(): Grid {
    const slots = Array.from({length: slotCount}, () => null)
    return {slots, hovered: null, focused: null, lastMove: null, rotation: null, recentItems: []}
}

function visibleNames(slots: Slots) {
    return new Set(slots.flatMap(operation => operation ? [operation.itemName] : []))
}

function isInspected(grid: Grid, index: number) {
    return index === grid.hovered || index === grid.focused
}

function refreshSlots(grid: Grid, operations: LandingOperation[]) {
    const byId = new Map(operations.map(operation => [operation.id, operation]))
    return grid.slots.map((operation, index) => operation
        ? byId.get(operation.id) ?? (isInspected(grid, index) ? operation : null) : null)
}

function slotOffset(from: number, to: number) {
    return {
        x: to % columns - from % columns,
        y: Math.floor(to / columns) - Math.floor(from / columns),
    }
}

function slotDistance(from: number, to: number) {
    const {x, y} = slotOffset(from, to)
    return Math.hypot(x, y)
}

function waveShift(from: number, to: number) {
    const {x, y} = slotOffset(from, to)
    const scale = waveOffsetPx / Math.hypot(x, y)
    return `${x * scale}px ${y * scale}px`
}

function reduceGrid(grid: Grid, action: Action): Grid {
    switch (action.type) {
        case 'refresh': {
            const slots = refreshSlots(grid, action.operations)
            const {rotation} = grid
            const canFinish = rotation && slots[rotation.from]?.id === grid.slots[rotation.from]?.id
                && slots[rotation.to] === null && action.operations.some(operation => operation.id === rotation.incoming.id)
            return {...grid, slots, rotation: canFinish ? rotation : null}
        }
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
        case 'fill': {
            const vacancies = grid.slots.flatMap((operation, index) => operation ? [] : [index])
            if (vacancies.length <= 1 || grid.rotation) return grid
            const available = distinctOperations(action.operations, visibleNames(grid.slots))
            const operation = available[Math.floor(action.randomOperation * available.length)]
            if (!operation) return grid

            const slots = [...grid.slots]
            slots[vacancies[Math.floor(action.randomSlot * vacancies.length)]] = operation
            return {...grid, slots}
        }
        case 'rotate': {
            if (grid.rotation) return grid
            const to = grid.slots.indexOf(null)
            if (to === -1) return grid

            const names = visibleNames(grid.slots)
            for (const item of grid.recentItems) {
                if (item.until > action.now) names.add(item.name)
            }
            const available = distinctOperations(action.operations, names)
            const incoming = available[Math.floor(action.randomOperation * available.length)]
            if (!incoming) return grid

            const distant = grid.slots.flatMap((slot, index) => slot && !isInspected(grid, index)
                && index !== grid.lastMove?.to ? [{index, name: slot.itemName}] : [])
                .sort((first, second) => slotDistance(second.index, to) - slotDistance(first.index, to))
                .slice(0, 2)
            const outgoing = distant[Math.floor(action.randomSlot * distant.length)]
            if (!outgoing) return grid
            return {...grid, rotation: {from: outgoing.index, to, incoming, outgoingName: outgoing.name}}
        }
        case 'finish': {
            if (grid.rotation !== action.rotation) return grid
            const {from, to, incoming, outgoingName} = action.rotation
            const slots = [...grid.slots]
            slots[from] = null
            slots[to] = incoming
            const recentItems = grid.recentItems.filter(item => item.until > action.now)
            recentItems.push({name: outgoingName, until: action.now + repeatDelayMs})
            return {...grid, slots, rotation: null, lastMove: {from, to}, recentItems}
        }
        case 'cancel':
            return grid.rotation ? {...grid, rotation: null} : grid
    }
}

export function useOperationRotation(operations: LandingOperation[], compactScreen: string) {
    const [grid, dispatch] = useReducer(reduceGrid, undefined, createGrid)
    const {rotation} = grid
    const latestOperations = useRef(operations)
    const availableCount = new Set(operations.map(operation => operation.itemName)).size
    const filling = grid.slots.filter(Boolean).length < Math.min(maxCards, availableCount)

    useEffect(() => {
        latestOperations.current = operations
        dispatch({type: 'refresh', operations})
    }, [operations])

    useEffect(() => {
        const compact = window.matchMedia(compactScreen)
        const motion = window.matchMedia('(prefers-reduced-motion: reduce)')
        let timer: number | undefined
        const fill = () => dispatch({type: 'fill', operations: latestOperations.current,
            randomSlot: Math.random(), randomOperation: Math.random()})
        const synchronize = () => {
            window.clearInterval(timer)
            if (document.hidden || compact.matches || motion.matches) {
                dispatch({type: 'cancel'})
                if (filling && motion.matches && !compact.matches && !document.hidden) {
                    for (let index = 0; index < maxCards; index++) fill()
                }
                return
            }
            if (filling) {
                timer = window.setInterval(fill, fillIntervalMs)
                return
            }
            timer = window.setInterval(() => dispatch({type: 'rotate', operations: latestOperations.current,
                randomSlot: Math.random(), randomOperation: Math.random(), now: Date.now()}), rotationIntervalMs)
        }
        synchronize()
        document.addEventListener('visibilitychange', synchronize)
        compact.addEventListener('change', synchronize)
        motion.addEventListener('change', synchronize)
        return () => {
            window.clearInterval(timer)
            document.removeEventListener('visibilitychange', synchronize)
            compact.removeEventListener('change', synchronize)
            motion.removeEventListener('change', synchronize)
        }
    }, [compactScreen, filling])

    useEffect(() => {
        if (!rotation) return
        const timer = window.setTimeout(() => dispatch({type: 'finish', rotation, now: Date.now()}), operationFadeOutMs)
        return () => window.clearTimeout(timer)
    }, [rotation])

    const shiftAt = (index: number) => {
        if (rotation?.from === index) return waveShift(rotation.from, rotation.to)
        if (grid.lastMove?.to === index) return waveShift(grid.lastMove.to, grid.lastMove.from)
    }

    return {slots: grid.slots, leavingSlot: rotation?.from, shiftAt,
        inspect: (interaction: Interaction, slot: number | null) => dispatch({type: 'inspect', interaction, slot, operations})}
}
