import {useCallback, useRef, useState} from 'react'
import {profileApi} from '../api/services'
import {ApiError} from '../api/client'
import type {SiteProfile} from '../../../shared/site/profile'

export function useProfileData() {
    const [profile, setProfile] = useState<SiteProfile | null>(null)
    const [profileError, setProfileError] = useState<string | null>(null)
    const [profileMissing, setProfileMissing] = useState(false)
    const owner = useRef<number | null>(null)
    const generation = useRef(0)
    const pending = useRef<Promise<SiteProfile | null> | null>(null)

    const clearProfile = useCallback(() => {
        generation.current++
        owner.current = null
        pending.current = null
        setProfile(null)
        setProfileError(null)
        setProfileMissing(false)
    }, [])

    const refreshProfile = useCallback((userId: number): Promise<SiteProfile | null> => {
        if (owner.current !== userId) {
            clearProfile()
            owner.current = userId
        }
        if (pending.current) return pending.current
        const currentGeneration = generation.current
        const request = profileApi.get().then(value => {
            if (generation.current !== currentGeneration) return null
            if (value.id !== userId) throw new Error('Профиль не соответствует текущему пользователю')
            setProfile(value)
            setProfileError(null)
            setProfileMissing(false)
            return value
        }).catch(cause => {
            if (generation.current !== currentGeneration) return null
            if (cause instanceof ApiError && [401, 403].includes(cause.status)) throw cause
            const missing = cause instanceof ApiError && cause.status === 404
            setProfile(null)
            setProfileMissing(missing)
            setProfileError(missing ? 'Чтобы создать профиль, войдите через Telegram ещё раз.' : 'Не удалось загрузить профиль.')
            return null
        }).finally(() => {
            if (pending.current === request) pending.current = null
        })
        pending.current = request
        return request
    }, [clearProfile])

    return {profile, profileError, profileMissing, refreshProfile, clearProfile}
}
