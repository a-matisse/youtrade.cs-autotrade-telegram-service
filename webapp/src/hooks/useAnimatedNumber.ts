import {useEffect, useRef, useState} from 'react'

export function useAnimatedNumber(target: number | null, {delay = 0, duration = 180, paused = false} = {}) {
  const [value, setValue] = useState(target)
  const current = useRef(target)

  useEffect(() => {
    if (target === null) {
      current.current = null
      setValue(null)
      return
    }
    if (paused || current.current === target) return
    if (current.current === null || window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      current.current = target
      setValue(target)
      return
    }

    const from = current.current
    let frame = 0
    const timer = window.setTimeout(() => {
      const started = performance.now()
      const animate = (time: number) => {
        const progress = Math.min(1, (time - started) / duration)
        const next = from + (target - from) * (1 - (1 - progress) ** 3)
        current.current = next
        setValue(next)
        if (progress < 1) frame = requestAnimationFrame(animate)
      }
      frame = requestAnimationFrame(animate)
    }, delay)

    return () => {
      window.clearTimeout(timer)
      cancelAnimationFrame(frame)
    }
  }, [target, delay, duration, paused])

  return target === null ? null : value ?? target
}
